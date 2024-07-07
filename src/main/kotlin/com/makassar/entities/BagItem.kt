package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


/**
 * Represents an item that composes a Bag.
 *
 * @property id The unique identifier of the bag item.
 * @property marketingName The name given to the item, can be useful for an item that would be sold separately . A shouldStrap could for example be labeled as "ALEO".
 * @property singleItemPrice Price if the item is sold separately, for a shoulderStrap for example.
 * @property ref Reference number of the item.
 * @property family Family to which the item belongs. A belong item can belong to a "shouldersStrap, handles, bodies,liners,etc".
 *
 *
 * @property colors List of colors present on the item, from principal to the least present color on the item.
 * @property measurements Measures of the item. Required string format: "Length:Width:Height". Distance should be provided in cm.
 * @property size Size of the item. Can be S (small), M (medium sized), L (large), etc.


 * @property materials To hold the list of materials ("ItemMaterial") from which the item can be built.
 * @property imageUrls Url's of images illustrating the item.
 */


@Serializable
data class BagItem(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val marketingName: String? = null,
    val singleItemPrice: String? = null,
    val ref: String? = null,
    val family: String? = null,

    val colors: List<String>? = null,
    val measurements: String? = null,
    val size: String? = null,



    val materials : Map<String,String>? = null, // "productId" : “quantity needed of that productMaterial"
    val imageUrls :  List<String>? = null,

    val description: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)