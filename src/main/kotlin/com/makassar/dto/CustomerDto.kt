package com.makassar.dto

import com.makassar.entities.User
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val name: String? = null,
    val mail: String? = null,
    val phone: String? = null,
    val country: String? = null,
    val shippingAddress: String? = null,
)