package com.makassar.entities

import com.makassar.dto.AddressDto
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

/**
 * Represents a client of makassar, could be an individual or an enterprise.
 *
 * @property name Name/alias given for the client.
 * @property mail Mail address of the client.
 * @property phone Phone number of the client.
 * @property shippingCountry Country where orders should be shipped.
 * @property shippingAddress Address of delivery of each orders related to that customer.
 * @property type Type of customer, could be a particular or enterprise.
*/


@Serializable
data class Customer(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val mail: String? = null,
    val phone: String? = null,
    val tva: String? = null,

    val professionalAddress: AddressDto? = null,
    val shippingAddress: AddressDto? = null,
    /*val professionalAddress: String? = null,
    val shippingCountry: String? = null,
    val shippingPostalCode: String? = null,*/
    val type:String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)