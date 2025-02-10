package com.makassar.entities


import com.makassar.dto.OrderProductDetailed
import com.makassar.dto.Price
import com.makassar.dto.requests.PlannedDate
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


/**
 * Represents an order made for a customer.
 *
 * @property id The unique identifier of the order.
 * @property customerId The unique identifier of the customer who made the order.
 * @property orderNumber The autoIncremented number given to each order.
 * @property status The current status of the order, can take "open", "preparation", "ready".
 * @property description A brief description of the order.
 * @property comments Any additional comments about the order.
 * @property totalPrice The price of the order (base price paid by the customer).
 * @property deliveryCost Additional costs for delivery to the charge of the customer.
 * @property discount Any discount applied to the order.
 * @property products A map of Product IDs to the quantity of the corresponding Product.
 * @property destination The destination where the order should be delivered.
 * @property dueDate The due date for the order.
 * @property createdAt The timestamp when the order was created.
 * @property updatedAt The timestamp when the order was last updated.
 */


@Serializable
data class Order(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val customerId: String? = null,
    val orderNumber: String? = null,

    val status :  String? = null,
    val price: Price? = null,
    val products : Map<String,String>? = null, // map of strings that are ids of the corresponding Product(s) to its quantity
    /*val plannedDate: PlannedDate? = null,*/
    val plannedDate: String? = null,

    val createdLocation: String? = null,

    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val description: String? = null,
    val comments: String? = null,
)




fun Order.toProductDetailedOrder(products : Map<Product,String>): OrderProductDetailed {
    return OrderProductDetailed(
        customerId = customerId,
        products = products,
        )
}