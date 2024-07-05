package com.makassar.services

interface CRUDService<dtoType,returnType> {
    suspend fun getAll(): List<returnType>

    suspend fun getOneById(id: String): returnType?

    suspend fun createOne(new: dtoType): String

    suspend fun updateOneById(id: String, updated: dtoType): Boolean

    suspend fun deleteOneById(id: String): Boolean
}


