package com.makassar.dto

import kotlinx.serialization.Serializable


@Serializable
data class BagItemDto(
    val marketingName: String? = null,
    val singleItemPrice: String? = null,
    val description: String? = null,

    val ref: String? = null,
    val family: String? = null,


    val colors: List<String>? = null,
    val measurements: String? = null,
    val size: String? = null,

    val materials : Map<String,String>? = null,
    val imageUrls :  List<String>? = null,


)