package com.raafi.muhasabahharian.core.utils

import com.google.firebase.auth.FirebaseAuth

object SessionManager {
    fun isUserSignedIn(): Boolean =
        FirebaseAuth.getInstance().currentUser != null
}
