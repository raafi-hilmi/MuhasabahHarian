package com.raafi.muhasabahharian.core.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.raafi.muhasabahharian.R
import com.raafi.muhasabahharian.core.model.User
import com.raafi.muhasabahharian.core.preference.UserPreferences

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var oneTapClient: SignInClient

    fun signInAnonymously(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseNetworkException -> "Tidak ada koneksi internet"
                        else -> "Login tamu gagal: ${exception?.message ?: "Terjadi kesalahan"}"
                    }
                    onFailure(Exception(errorMessage))
                }
            }
    }

    fun signInWithGoogle(
        activity: Activity,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            oneTapClient = Identity.getSignInClient(activity)

            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(activity.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()

            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(activity) { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        val launcher = createSignInLauncher(activity, onSuccess, onFailure)
                        launcher.launch(intentSenderRequest)
                    } catch (e: Exception) {
                        onFailure(Exception("Gagal memulai sign-in: ${e.message}"))
                    }
                }
                .addOnFailureListener(activity) { e ->
                    val errorMessage = when (e) {
                        is ApiException -> {
                            when (e.statusCode) {
                                CommonStatusCodes.CANCELED -> "Sign-in dibatalkan"
                                CommonStatusCodes.NETWORK_ERROR -> "Kesalahan jaringan"
                                CommonStatusCodes.TIMEOUT -> "Waktu habis, coba lagi"
                                CommonStatusCodes.DEVELOPER_ERROR -> "Kesalahan konfigurasi"
                                else -> "Error Google Sign-In: ${e.statusCode}"
                            }
                        }
                        else -> "Terjadi kesalahan: ${e.message}"
                    }
                    onFailure(Exception(errorMessage))
                }
        } catch (e: Exception) {
            onFailure(Exception("Inisialisasi sign-in gagal: ${e.message}"))
        }
    }

    private fun createSignInLauncher(
        activity: Activity,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (Exception) -> Unit
    ): ActivityResultLauncher<IntentSenderRequest> {
        return (activity as ComponentActivity).activityResultRegistry.register(
            "googleSignInLauncher",
            androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onSuccess(auth.currentUser)
                                } else {
                                    val exception = task.exception
                                    val errorMsg = when (exception) {
                                        is FirebaseAuthInvalidCredentialsException -> "Kredensial tidak valid"
                                        is FirebaseAuthUserCollisionException -> "Akun sudah terdaftar"
                                        is FirebaseNetworkException -> "Tidak ada koneksi internet"
                                        else -> "Autentikasi gagal: ${exception?.message}"
                                    }
                                    onFailure(Exception(errorMsg))
                                }
                            }
                    } else {
                        onFailure(Exception("ID Token tidak ditemukan"))
                    }
                } catch (e: ApiException) {
                    onFailure(Exception("Gagal mendapatkan kredensial: ${e.message}"))
                }
            }
        }
    }

    suspend fun cacheUser(context: Context, firebaseUser: FirebaseUser) {
        val user = User(
            uid         = firebaseUser.uid,
            name        = firebaseUser.displayName ?: "Tamu",
            email       = firebaseUser.email,
            isAnonymous = firebaseUser.isAnonymous
        )
        UserPreferences.save(context, user)
    }
    fun signOut() = auth.signOut()
    fun getCurrentUser() = auth.currentUser
}