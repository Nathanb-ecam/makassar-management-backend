
import com.makassar.dto.BagPartDto
import com.makassar.utils.FileUploadProcessing
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.bagSubPartRoutes(
    bagPartService : BagPartService
) {

    val allowedFileTypesString = environment.config.tryGetString("allowedUploadFileTypes") ?: "png,jpg,jpeg"

    routing {
        authenticate("access-jwt"){
            route("/api/bag-parts"){

                post{
                    try{
                        val bagPart = call.receive<BagPartDto>()
                        if(bagPart.family == null) return@post call.respond(HttpStatusCode.BadRequest,"Bag part requires property 'family'")

                        val id = bagPartService.createOne(bagPart)
                        call.respond(HttpStatusCode.OK, mapOf("bagPartId" to id))
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }
                }

                post("/withImages"){
                    val multipart = call.receiveMultipart()
                    var bagSubPartDto: BagPartDto? = null
                    val fileParts = mutableListOf<PartData.FileItem>()

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                if(part.name =="data"){
                                    bagSubPartDto = Json.decodeFromString<BagPartDto>(part.value)
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    fileParts.add(part)
                                }
                            }
                            else -> {}
                        }
                        part.dispose()
                    }

                    if (bagSubPartDto == null) return@post call.respond(HttpStatusCode.BadRequest, "Bag information were not provided")

                    if(bagSubPartDto!!.family == null){
                        return@post call.respond(HttpStatusCode.BadRequest, "BagSubPart requires property 'family'")
                    }

                    val fileUploadResult = FileUploadProcessing.handleFileUploads("bags-subparts",fileParts,allowedFileTypesString)
                    val uploadedImagesUrls = fileUploadResult["imageUrls"]?.toList()


                    bagSubPartDto = bagSubPartDto!!.copy(
                        imageUrls =  uploadedImagesUrls,
                    )

                    val id = bagPartService.createOne(bagSubPartDto!!)
                    if(fileUploadResult["fileExtensionNotAllowed"]!!.isNotEmpty()) call.respond(HttpStatusCode.Created, mapOf( "bagSubPartId" to id, "errors" to "File extensions not allowed : ${fileUploadResult["fileExtensionNotAllowed"]}"))
                    else call.respond(HttpStatusCode.Created, mapOf("bagSubPartId" to id))
                }



                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val bagPart = bagPartService.getOneById(id)
                        if (bagPart != null) {
                            call.respond(bagPart)
                        }
                        call.respond(HttpStatusCode.NotFound)

                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                get {
                    try {
                        val BagPart = bagPartService.getAll()
                        if (BagPart.isEmpty()) {
                            call.respond("No BagPart found")
                        }
                        call.respond(HttpStatusCode.OK, BagPart)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val BagPart = call.receive<BagPartDto>()
                        bagPartService.updateOneById(id, BagPart).let {
                            val result =  if(it)  "Successfully modified BagPart with id $id"  else "BagPart with id $id not found"
                            call.respond(HttpStatusCode.OK,result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                delete("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        bagPartService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted BagPart with id $id"  else "BagPart with id $id not found"
                            call.respond(HttpStatusCode.OK, result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }
            }
        }


    }
}