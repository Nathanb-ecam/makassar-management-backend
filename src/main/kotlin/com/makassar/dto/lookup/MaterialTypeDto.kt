package com.makassar.dto.lookup

import kotlinx.serialization.Serializable



@Serializable
data class MaterialTypeDto(
    val name: String? = null,
    val description: String? = null,

)