package com.makassar.utils

import io.ktor.http.content.*
import java.io.File
import java.util.*

object FileProcessing {
    fun handleFileUploads(uploadFolderName: String, fileParts: MutableList<PartData.FileItem>, allowedFileTypesString : String) : Map<String,Set<String>>{
        val fileTypeErr = mutableSetOf<String>()
        val imageUrls = mutableSetOf<String>()

        fileParts.forEach { filePart ->
            try{
                val ext = filePart.originalFileName?.let { it1 -> File(it1).extension.lowercase() } ?: "png"

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


                filePart.streamProvider().use { streamProvider ->
                    file.outputStream().buffered().use {
                        streamProvider.copyTo(it)
                    }
                }

                imageUrls.add("$uploadFolderName/$servedFileName")


            }catch (e: Exception){
                e.printStackTrace()
            }
            filePart.dispose()
        }

        return mapOf(
            "fileExtensionNotAllowed" to fileTypeErr.toSet(),
            "imageUrls" to imageUrls.toSet()
            )
    }


    fun deleteUploadedFilesWithNames(imageUrls : Set<String>) : Boolean{
        val deletedFiles = mutableSetOf<String>()
        imageUrls.forEach {imageUrl ->
            try{

            val file = File("uploads/$imageUrl")
            if(file.exists()){
                val deleted = file.delete()
                if (deleted) deletedFiles.add(imageUrl)
            }
            }catch(e: Exception){
                e.printStackTrace()

            }
        }

        return imageUrls.size == deletedFiles.size
    }
}