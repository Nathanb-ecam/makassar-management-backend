package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Serializable
data class ProductMaterial(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val color: String? = null,
    val pricePerUnit: String? = null,
    val dimensions: String? = null,
    val surface: String? = null,
    val description: String? = null,
    val resellers: List<String>? = null,

)