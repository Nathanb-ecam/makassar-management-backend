package com.makassar.entities.countersLookup

import org.bson.codecs.pojo.annotations.BsonId

/**
* @property id  is the identifier of the entity that needs a counter.
 * @property sequenceValue is the current value of the counter.
* */

data class Sequence(
    @BsonId val id: String,
    val sequenceValue: Long
)
