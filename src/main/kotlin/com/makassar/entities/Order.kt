package com.makassar.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*


/**
 * Represents an order made for a customer.
 *
 * @property id The unique identifier of the order.
 * @property customerId The unique identifier of the customer who made the order.
 * @property status The current status of the order, can take "open", "preparation", "ready".
 * @property description A brief description of the order.
 * @property comments Any additional comments about the order.
 * @property totalPrice The price of the order (base price paid by the customer).
 * @property deliveryCost Additional costs for delivery to the charge of the customer.
 * @property discount Any discount applied to the order.
 * @property bags A map of bag IDs to the quantity of the corresponding bag.
 * @property destination The destination where the order should be delivered.
 * @property dueDate The due date for the order.
 * @property createdAt The timestamp when the order was created.
 * @property updatedAt The timestamp when the order was last updated.
 */


@Serializable
data class Order(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val customerId: String? = null,
    val status :  String? = null,
    val totalPrice: String? = null,
    val deliveryCost: String? = null,
    val discount: String? = null,
    val bags : Map<String,String>? = null, // list of strings that are ids of the corresponding Product(s)
    val plannedDate: String? = null,

    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val description: String? = null,
    val comments: String? = null,
)