package com.makassar.dto.lookup

import kotlinx.serialization.Serializable



@Serializable
data class ColorDto(
    val name: String? = null,
    val hexCode: String? = null,
)