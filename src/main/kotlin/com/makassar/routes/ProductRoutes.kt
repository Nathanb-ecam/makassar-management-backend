
import com.makassar.dto.ProductDto
import com.makassar.dto.requests.StringListRequest
import com.makassar.dto.toEntity
import com.makassar.utils.FileProcessing
import com.makassar.utils.FileProcessing.deleteUploadedFilesWithNames
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

fun Application.productRoutes(
    productService : ProductService
) {

    val allowedFileTypesString = environment.config.tryGetString("allowedUploadFileTypes") ?: "png,jpg,jpeg"
    val logger = LoggerFactory.getLogger("productRoutes")
    routing {
        authenticate("access-jwt"){
            route("/api"){
                post("/{tenantId}/products"){
                    try{
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val productPart = call.receive<ProductDto>()
                        if(productPart.marketingName == null) return@post call.respond(HttpStatusCode.BadRequest,"product requires property 'marketingName'")
                        if(productPart.userId == null) return@post call.respond(HttpStatusCode.BadRequest,"tenantId was not specified")

                        val id = productService.createOne(userId, productPart)
                        call.respond(HttpStatusCode.OK, mapOf("productId" to id))
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }
                }


                post("/{tenantId}/products/withImages"){
                    val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                    val multipart = call.receiveMultipart()
                    var ProductDto: ProductDto? = null
                    val fileParts = mutableListOf<PartData.FileItem>()

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                if(part.name =="data"){
                                    ProductDto = Json.decodeFromString<ProductDto>(part.value)
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "image") {
                                    fileParts.add(part)
                                }
                            }
                            else -> {logger.debug("Unhandled part ${part.name}")}
                        }
                    }

                    if (ProductDto == null) return@post call.respond(HttpStatusCode.BadRequest, "product information were not provided")

                    if(ProductDto!!.marketingName == null) return@post call.respond(HttpStatusCode.BadRequest, "product requires property 'marketingName'")


                    //if(ProductDto!!.userId == null) return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified")


                    val fileUploadResult = FileProcessing.handleFileUploads("$userId/products",fileParts,allowedFileTypesString)
                    val uploadedImagesUrls = fileUploadResult["imageUrls"]?.toList()


                    ProductDto = ProductDto!!.copy(imageUrls =  uploadedImagesUrls, userId = userId)

                    val id = productService.createOne(userId, ProductDto!!)


                    if(fileUploadResult["fileExtensionNotAllowed"]!!.isNotEmpty()) call.respond(HttpStatusCode.Created, mapOf( "productId" to id, "errors" to "File extensions not allowed : ${fileUploadResult["fileExtensionNotAllowed"]}"))
                    else call.respond(HttpStatusCode.Created, mapOf("product" to ProductDto!!.toEntity(id)))

                }

                get("/{tenantId}/products/{id}") {

                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val product = productService.getOneById(userId, id)
                        if (product != null) {
                            call.respond(product)
                        }
                        call.respond(HttpStatusCode.NotFound)

                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

            get("/{tenantId}/products") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val product = productService.getAll(userId)
                        if (product.isEmpty()) {
                            call.respond("No product found")
                        }
                        call.respond(HttpStatusCode.OK, product)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                post("/{tenantId}/products/withIds"){
                    try {
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val productIds = call.receive<StringListRequest>()
                        val products = productService.getAllByIds(userId, productIds.stringList)
                        if (products.isEmpty()) {
                            call.respond("No product found for those ids")
                        }
                        call.respond(HttpStatusCode.OK, products)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }


                put("/{tenantId}/products/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val product = call.receive<ProductDto>()
                        productService.updateOneById(userId, id, product).let {
                            val result =  if(it)  "Successfully modified product with id $id"  else "product with id $id not found"
                            call.respond(HttpStatusCode.OK,result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                delete("/{tenantId}/products/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val imageUrls = call.receive<StringListRequest>()
                        productService.deleteOneById(userId, id).let {
                            if(it) {
                                val filesDeleted = deleteUploadedFilesWithNames(imageUrls.stringList.toSet())
                                if(filesDeleted) call.respond(HttpStatusCode.OK,mapOf("productId" to id))
                                else call.respond(HttpStatusCode.OK, mapOf("productId" to id, "err" to "some images haven't been removed"))
                            }

                            else call.respond(HttpStatusCode.OK, "product with id $id not found")

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