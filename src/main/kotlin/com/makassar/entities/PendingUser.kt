package com.makassar.entities


import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

@Serializable
data class PendingUser(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val otp : String, // One Time Password
    val username: String? = null,
    val mail: String? = null,
    val passwordHash: String? = null,
    val roles : Set<String>? = null,
    val createdAt: Long? = null,
    //val updatedAt: Long? = null,
)

