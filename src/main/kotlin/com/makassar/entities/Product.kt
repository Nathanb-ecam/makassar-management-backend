package com.makassar.entities

import com.makassar.dto.ProductMaterialDto
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Serializable
data class Product(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val price: String? = null,
    val description: String? = null,
    val ref : String? = null,
    val materials : Map<String,String>? = null, // "productId" : “quantity needed of that productMaterial"
    val imageUrl :  List<String>? = null,
)