package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductMaterialDto(
    val name: String? = null,
    val color: String? = null,
    val pricePerUnit: String? = null,
    val dimensions: String? = null,
    val surface: String? = null,
    val description: String? = null,
    val resellers: List<String>? = null,
)