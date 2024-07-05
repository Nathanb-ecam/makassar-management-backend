package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val roles : List<String>? = null,
)