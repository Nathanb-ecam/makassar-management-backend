package com.makassar.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmUserRequest(
    val mail: String,
    val otp: String,
)