package com.makassar.dto

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val name: String? = null,
    val mail: String? = null,
    val phone: String? = null,
    val tva: String? = null,
/*    val professionalAddress: AddressDto? = null,
    val shippingAddress: AddressDto? = null,*/
    val professionalAddress: String? = null,
    val shippingAddress: String? = null,
    val type:String? = null

)