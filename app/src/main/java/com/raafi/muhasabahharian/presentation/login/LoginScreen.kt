package com.raafi.muhasabahharian.presentation.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raafi.muhasabahharian.R
import com.raafi.muhasabahharian.core.auth.AuthManager
import com.raafi.muhasabahharian.core.auth.AuthManager.cacheUser
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onGuestClick: () -> Unit = {},
    onGoogleSuccess: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logomuhasabah),
                contentDescription = "Muhasabah Harian Logo",
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Guest Login Button
            Button(
                onClick = {
                    isLoading = true
                    AuthManager.signInAnonymously(
                        onSuccess = {
                            scope.launch {
                                AuthManager.getCurrentUser()?.let { u ->
                                    cacheUser(context, u)
                                }
                                isLoading = false
                                onGuestClick()
                            }
                        },
                        onFailure = { e ->
                            isLoading = false
                            errorMessage = "Login tamu gagal: ${e.message}"
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Masuk sebagai Tamu",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    isLoading = true
                    AuthManager.signInWithGoogle(
                        activity = activity,
                        onSuccess = { user ->
                            if (user != null) {
                                scope.launch {
                                    cacheUser(context, user)
                                    isLoading = false
                                    onGoogleSuccess()
                                }
                            } else {
                                isLoading = false
                                errorMessage = "Autentikasi Google gagal"
                            }
                        },
                        onFailure = { e ->
                            isLoading = false
                            errorMessage = "Login Google gagal: ${e.message}"
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Masuk dengan Google",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // Tampilkan pesan error
            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}