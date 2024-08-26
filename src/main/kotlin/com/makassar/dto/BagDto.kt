package com.makassar.dto

import com.makassar.entities.Bag
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class BagDto(
    val marketingName: String? = null,
    val retailPrice: String? = null,
    val wholesalePrice: String? = null,
    val description: String? = null,
    val sku: String? = null,
    val colors: List<String>? = null,


    val handles: Map<String,String>? = null,
    val bodies: Map<String,String>? = null,
    val shoulderStraps: Map<String,String>? = null,
    val figures: Map<String,String>? = null,
    val liners: Map<String,String>? = null,
    val screws: Map<String,String>? = null,
    val others: Map<String,String>? = null,

    val materials : Map<String,String>? = null,
    val imageUrls :  List<String>? = null,
)


fun BagDto.toEntity(id : String): Bag {
    return Bag(
        id = id,
        marketingName = this.marketingName,
        retailPrice = this.retailPrice,
        wholesalePrice = this.wholesalePrice,
        sku = this.sku,
        colors = this.colors,
        handles = this.handles,
        bodies = this.bodies,
        shoulderStraps = this.shoulderStraps,
        figures = this.figures,
        liners = this.liners,
        screws = this.screws,
        others = this.others,
        materials = this.materials,
        imageUrls = this.imageUrls,
        description = this.description,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

}

