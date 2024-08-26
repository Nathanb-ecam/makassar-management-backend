package com.makassar.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class StringListRequest(val stringList : List<String>)
