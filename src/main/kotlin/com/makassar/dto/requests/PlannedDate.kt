package com.makassar.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class PlannedDate(
    val best : String,
    val worst : String,
)
