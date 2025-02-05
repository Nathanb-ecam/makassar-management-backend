package com.makassar.services

import org.bson.types.ObjectId
import java.security.Provider
import java.util.*

interface GenericService<dtoType,returnType> {
    suspend fun getAll(userId : String): List<returnType>

    suspend fun getOneById(userId : String, id: String): returnType?

    suspend fun createOne(new: dtoType): String

    suspend fun updateOneById(userId : String, id: String, updated: dtoType): Boolean

    suspend fun deleteOneById(userId : String, id: String): Boolean
}

