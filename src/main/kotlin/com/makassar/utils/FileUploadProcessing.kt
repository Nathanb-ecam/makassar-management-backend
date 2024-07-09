package com.makassar.utils

import io.ktor.http.content.*
import java.io.File
import java.util.*

object FileUploadProcessing {
    fun handleFileUploads(uploadFolderName: String, fileParts: MutableList<PartData.FileItem>, allowedFileTypesString : String) : Map<String,Set<String>>{
        val fileTypeErr = mutableSetOf<String>()
        val imageUrls = mutableSetOf<String>()

        fileParts.forEach { filePart ->
            try{
                val ext = filePart.originalFileName?.let { it1 -> File(it1).extension } ?: "png"

                val allowedFileTypes = allowedFileTypesString.split(",").toList()
                if(!allowedFileTypes.contains(ext)){
                    fileTypeErr.add(ext)
                    return@forEach
                }


                val originalFileName = filePart.originalFileName ?: UUID.randomUUID().toString()
                val servedFileName = "${System.currentTimeMillis()}-$originalFileName"

                val filePath = "uploads/$uploadFolderName/$servedFileName"
                val file = File(filePath)

                file.parentFile.mkdirs()
                file.outputStream().buffered().use {
                    filePart.streamProvider().use { input -> input.copyTo(it) }
                }

                imageUrls.add("$uploadFolderName/$servedFileName")


            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        return mapOf(
            "fileExtensionNotAllowed" to fileTypeErr.toSet(),
            "imageUrls" to imageUrls.toSet()
            )
    }
}