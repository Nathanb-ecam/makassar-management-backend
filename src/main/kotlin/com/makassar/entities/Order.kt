package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Serializable
data class Order(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val customerId: String? = null,
    val description: String? = null,
    val comments: String? = null,
    val price: String? = null,
    val discount: String? = null,
    val products : Map<String,String>? = null, // list of strings that are ids of the corresponding Product(s)
    val destination: String? = null,
    val dueDate: String? = null,

)