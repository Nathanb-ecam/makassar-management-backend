package com.makassar.dto

import kotlinx.serialization.Serializable


@Serializable
data class BagDto(
    val marketingName: String? = null,
    val retailPrice: String? = null,
    val description: String? = null,
    val sku: String? = null,

    val handles: Map<String,String>? = null,
    val bodies: Map<String,String>? = null,
    val shoulderStraps: Map<String,String>? = null,
    val figures: Map<String,String>? = null,
    val liners: Map<String,String>? = null,
    val screws: Map<String,String>? = null,
    val others: Map<String,String>? = null,

    val materials : Map<String,String>? = null,
    val imageUrls :  List<String>? = null,
)