package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val country: String? = null,
    val postalCode: String? = null,
    val address: String? = null,
)