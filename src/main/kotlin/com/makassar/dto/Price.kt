package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class Price(
    val finalPrice: String? = null,
    val alreadyPaid: String? = null,
    val deliveryCost: String? = null,
    val discount: String? = null,
)
