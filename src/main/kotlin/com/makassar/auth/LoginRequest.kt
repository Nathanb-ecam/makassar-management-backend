package com.makassar.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val mail: String,
    val password: String,
)