package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val customerId: String? = null,
    val status :  String? = null,
    val description: String? = null,
    val comments: String? = null,
    val totalPrice: String? = null,
    val deliveryCost: String? = null,
    val discount: String? = null,
    val bags : Map<String,String>? = null, // "bagId" : quantity
    val plannedDate: String? = null,

)