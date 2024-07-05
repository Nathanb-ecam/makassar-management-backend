package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val customerId: String? = null,
    val description: String? = null,
    val comments: String? = null,
    val price: String? = null,
    val discount: String? = null,
    val products : Map<String,String>? = null, // "productId" : quantity
    val destination: String? = null,
    val dueDate: String? = null,
)