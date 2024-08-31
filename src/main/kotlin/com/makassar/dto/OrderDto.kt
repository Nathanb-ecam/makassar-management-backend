package com.makassar.dto

import com.makassar.entities.Bag
import com.makassar.entities.Customer
import com.makassar.entities.Order
import io.ktor.client.utils.EmptyContent.status
import kotlinx.serialization.Serializable

@Serializable
open class OrderDto(
    val customerId: String? = null,
    val orderNumber: String? = null,
    val createdLocation: String? = null,
    val status :  String? = null,
    val description: String? = null,
    val comments: String? = null,
    val price : Price? = null,
    val bags : Map<String,String>? = null, // "bagId" : quantity
    /*val plannedDate: PlannedDate? = null,*/
    val plannedDate: String? = null,
)





@Serializable
data class OrderOverview(
    val id: String? = null,
    val customerName: String? = null,
    val orderNumber: String? = null,
    val status :  String? = null,
    val price : Price? = null,
    /*val plannedDate: PlannedDate? = null,*/
    val plannedDate: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)


@Serializable
data class OrderFullyDetailed(
    val id: String? = null,
    val customer: Customer? = null,
    val orderNumber: String? = null,
    val status :  String? = null,
    val description: String? = null,
    val comments: String? = null,
    val price : Price? = null,
    val bags : Map<String,BagWithQuantity>? = null, // "bagId" : quantity
    val plannedDate: String? = null,
    val createdLocation: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)

@Serializable
data class OrderCustomerDetailed(
    val id: String? = null,
    val customer: Customer? = null,
    val createdLocation: String? = null,
    val orderNumber: String? = null,
    val description: String? = null,
    val status : String? = null,
    val comments: String? = null,
    val price : Price? = null,
    val bags : Map<String,String>? = null, // "bagId" : quantity
    val plannedDate: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)


/*fun OrderCustomerDetailed.toFullyDetailed(bags: List<BagWithQuantity>) : OrderFullyDetailed {*/
fun OrderCustomerDetailed.toFullyDetailed(bags: Map<String,BagWithQuantity>) : OrderFullyDetailed {
    return OrderFullyDetailed(
        id = id,
        customer = customer,
        orderNumber = orderNumber,
        status = status,
        description = description,
        comments = comments,
        price = price,
        bags = bags,
        plannedDate = plannedDate,
        createdLocation = createdLocation,
        createdAt = createdAt,
        updatedAt = updatedAt

    )
}

data class OrderBagDetailed(
    val customerId: String? = null,
    val orderNumber: String? = null,

    val status :  String? = null,
    val price : Price? = null,
    val bags : Map<Bag,String>? = null,
    /*val plannedDate: PlannedDate? = null,*/
    val plannedDate: String? = null,

    val createdLocation: String? = null,

    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val description: String? = null,
    val comments: String? = null,

    )


