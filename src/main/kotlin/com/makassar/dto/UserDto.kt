package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String? = null,
    val mail: String? = null,
    val password: String? = null,
    val roles : Set<String>? = null,
)