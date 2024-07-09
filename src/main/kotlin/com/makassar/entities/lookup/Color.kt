package com.makassar.entities.lookup


import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


/**
 * Represents an item that composes a Bag.
 *
 * @property id The unique identifier of the bag item.
 * @property name Name of the color.
 * @property hexCode Color code with hex format. Ex: #FEFEFE

 */


@Serializable
data class Color(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val hexCode: String? = null,
)