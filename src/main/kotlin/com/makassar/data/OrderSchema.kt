package com.makassar.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.bson.Document

@Serializable
data class Order(
    val brandName: String,
    val model: String,
    val number: String
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Order = json.decodeFromString(document.toJson())
    }
}

