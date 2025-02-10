
import com.makassar.dto.ProductPartDto
import com.makassar.utils.FileProcessing
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.productSubPartRoutes(
    productPartService : ProductPartService
) {

    val allowedFileTypesString = environment.config.tryGetString("allowedUploadFileTypes") ?: "png,jpg,jpeg"

    routing {
        authenticate("access-jwt"){
            route("/api"){

                post("/{tenantId}/product-parts"){
                    try{
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val productPart = call.receive<ProductPartDto>()
                        if(productPart.family == null) return@post call.respond(HttpStatusCode.BadRequest,"product part requires property 'family'")
                        if(productPart.userId == null) return@post call.respond(HttpStatusCode.BadRequest,"tenantId was not specified")

                        val id = productPartService.createOne(userId, productPart)
                        call.respond(HttpStatusCode.OK, mapOf("productPartId" to id))
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }
                }

                post("/{tenantId}/product-parts/withImages"){
                    val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                    val multipart = call.receiveMultipart()
                    var productSubPartDto: ProductPartDto? = null
                    val fileParts = mutableListOf<PartData.FileItem>()

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                if(part.name =="data"){
                                    productSubPartDto = Json.decodeFromString<ProductPartDto>(part.value)
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

                    if (productSubPartDto == null) return@post call.respond(HttpStatusCode.BadRequest, "product information were not provided")

                    if(productSubPartDto!!.family == null) return@post call.respond(HttpStatusCode.BadRequest, "productSubPart requires property 'family'")


                    //if(productSubPartDto!!.userId == null) return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified")


                    val fileUploadResult = FileProcessing.handleFileUploads("$userId/products-subparts",fileParts,allowedFileTypesString)
                    val uploadedImagesUrls = fileUploadResult["imageUrls"]?.toList()


                    productSubPartDto = productSubPartDto!!.copy(
                        imageUrls =  uploadedImagesUrls,
                        userId = userId
                    )

                    val id = productPartService.createOne(userId, productSubPartDto!!)
                    if(fileUploadResult["fileExtensionNotAllowed"]!!.isNotEmpty()) call.respond(HttpStatusCode.Created, mapOf( "productSubPartId" to id, "errors" to "File extensions not allowed : ${fileUploadResult["fileExtensionNotAllowed"]}"))
                    else call.respond(HttpStatusCode.Created, mapOf("productSubPartId" to id))
                }



                get("/{tenantId}/product-parts/{id}") {

                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val productPart = productPartService.getOneById(userId, id)
                        if (productPart != null) {
                            call.respond(productPart)
                        }
                        call.respond(HttpStatusCode.NotFound)

                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                get("/{tenantId}/product-parts") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val productPart = productPartService.getAll(userId)
                        if (productPart.isEmpty()) {
                            call.respond("No productPart found")
                        }
                        call.respond(HttpStatusCode.OK, productPart)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("/{tenantId}/product-parts/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val productPart = call.receive<ProductPartDto>()
                        productPartService.updateOneById(userId, id, productPart).let {
                            val result =  if(it)  "Successfully modified productPart with id $id"  else "productPart with id $id not found"
                            call.respond(HttpStatusCode.OK,result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                delete("/{tenantId}/product-parts/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        productPartService.deleteOneById(userId, id).let {
                            val result =  if(it)  "Successfully deleted productPart with id $id"  else "productPart with id $id not found"
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