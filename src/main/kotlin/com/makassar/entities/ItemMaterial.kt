package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


/**
* @property name Name of the material object.
 * @property color Main color of the material object.
 * @property materialType Type of material. Ex: leather, plastic, etc.
 * @property measurements Dimensionjs of the item. String format Length:Width:Height. Dimensions in centimeters.
 * @property surface Area of the item, in cm2.
 * @property resellers List of resellers of the material.
 */

@Serializable
data class ItemMaterial(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val materialType: String? = null,
    val color : String? = null,
    val measurements: String? = null,
    val surface: String? = null,
    val description: String? = null,
    val resellers: List<String>? = null, // list of Ids of the sellers of the material
    val createdAt: Long? = null,
    val updatedAt: Long? = null,

)