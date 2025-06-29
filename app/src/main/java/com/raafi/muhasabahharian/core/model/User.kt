package com.raafi.muhasabahharian.core.model

data class User(
    val uid: String,
    val name: String,
    val email: String? = null,
    val isAnonymous: Boolean
)