package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val name: String? = null,
    val price: String? = null,
    val description: String? = null,
    val ref : String? = null,
    val materials : Map<String,String>? = null, // "productId" : “quantity needed of that productMaterial"
    val imageUrl :  List<String>? = null,
)