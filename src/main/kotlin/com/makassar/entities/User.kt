package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Serializable
data class User(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val username: String? = null,
    val email: String? = null,
    val passwordHash: String? = null,
    val roles : List<String>? = null,
)

