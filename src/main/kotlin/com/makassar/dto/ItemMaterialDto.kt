package com.makassar.dto

import kotlinx.serialization.Serializable



@Serializable
data class ItemMaterialDto(
    val name: String? = null,
    val materialType: String? = null,
    val color: String? = null,

    val measurements: String? = null,
    val surface: String? = null,

    val description: String? = null,
    val resellers: List<String>? = null,
)