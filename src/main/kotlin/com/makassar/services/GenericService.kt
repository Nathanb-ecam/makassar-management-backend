package com.makassar.services

import org.bson.types.ObjectId
import java.security.Provider
import java.util.*

interface GenericService<dtoType,returnType> {
    suspend fun getAll(): List<returnType>

    suspend fun getOneById(id: String): returnType?

    suspend fun createOne(new: dtoType): String

    suspend fun updateOneById(id: String, updated: dtoType): Boolean

    suspend fun deleteOneById(id: String): Boolean
}

