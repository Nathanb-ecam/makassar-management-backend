package com.makassar.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class IdsRequest(val ids : List<String> )
