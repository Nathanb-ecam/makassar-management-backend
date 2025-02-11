package com.makassar.dto

import com.makassar.entities.Product
import kotlinx.serialization.Serializable


@Serializable
data class ProductDto(
    var userId: String? = null,
    val marketingName: String? = null,
    val retailPrice: String? = null,
    val wholesalePrice: String? = null,
    val description: String? = null,
    val sku: String? = null,
    val colors: List<String>? = null,
    val materials : Map<String,String>? = null,
    val imageUrls :  List<String>? = null,
)


fun ProductDto.toEntity(id : String): Product {
    return Product(
        id = id,
        marketingName = this.marketingName,
        retailPrice = this.retailPrice,
        wholesalePrice = this.wholesalePrice,
        sku = this.sku,
        colors = this.colors,
        imageUrls = this.imageUrls,
        description = this.description,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

}


@Serializable
data class ProductWithQuantity(
    val product: Product? = null,
    val quantity : String? = null
)




