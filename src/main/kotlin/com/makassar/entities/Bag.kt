package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


/**
 * Represents a Bag product.
 *
 * @property id The unique identifier of the bag.
 * @property marketingName The name of the bag to be showed to customers.
 * @property retailPrice Retail price of the bag.
 * @property sku Reference number of the bag.

 Links to other items.

 * @property handles Map of the BagItem Id that is from the "handles" family, to the quantity of the item.
 * @property bodies Map of the BagItem Id that is from the "bodies" family, to the quantity of the item.
 * @property shoulderStraps Map of the BagItem Id that is from the "shoulderStraps" family, to the quantity of the item.
 * @property figures Map of the BagItem Id that is from the "figures" family, to the quantity of the item.
 * @property liners Map of the BagItem Id that is from the "liners" family, to the quantity of the item.
 * @property screws Map of the BagItem Id that is from the "screws" family, to the quantity of the item.
 * @property others Map of the BagItem Id that is from the "others" family, to the quantity of the item.

 * @property materials To hold the full list of materials needed to build the bag (computed at creation of the entity by summing all the materials
 * needed for the BagItems).
 * @property imageUrls Url's of images illustrating the bag.
 */


@Serializable
data class Bag(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val marketingName: String? = null,
    val retailPrice: String? = null,
    val sku : String? = null,

    val handles: Map<String,String>? = null,
    val bodies: Map<String,String>? = null,
    val shoulderStraps: Map<String,String>? = null,
    val figures: Map<String,String>? = null,
    val liners: Map<String,String>? = null,
    val screws: Map<String,String>? = null,
    val others: Map<String,String>? = null,

    val materials : Map<String,String>? = null, // "productId" : “quantity needed of that productMaterial"
    val imageUrls :  List<String>? = null,

    val description: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)

/*

    val handles: Map<String,String>? = null,
    val bodies: Map<String,String>? = null,
    val shoulderStraps: Map<String,String>? = null,
    val figures: Map<String,String>? = null,
    val liners: Map<String,String>? = null,
    val screws: Map<String,String>? = null,
    val others: Map<String,String>? = null,

    val materials : Map<String,String>? = null,
    val imageUrls :  List<String>? = null,
 */