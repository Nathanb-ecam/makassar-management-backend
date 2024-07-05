package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Serializable
data class Customer(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val mail: String? = null,
    val phone: String? = null,
    val country: String? = null,
    val shippingAddress: String? = null,
)