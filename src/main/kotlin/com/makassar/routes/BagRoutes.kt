
import com.makassar.dto.BagDto
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

fun Application.bagRoutes(
    bagService : BagService
) {

    val allowedFileTypesString = environment.config.tryGetString("allowedUploadFileTypes") ?: "png,jpg,jpeg"
    val logger = LoggerFactory.getLogger("BagRoutes")
    routing {
        authenticate("access-jwt"){
            route("/api"){
                post("/{tenantId}/bags"){
                    try{
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val bagPart = call.receive<BagDto>()
                        if(bagPart.marketingName == null) return@post call.respond(HttpStatusCode.BadRequest,"Bag requires property 'marketingName'")
                        if(bagPart.userId == null) return@post call.respond(HttpStatusCode.BadRequest,"tenantId was not specified")

                        val id = bagService.createOne(userId, bagPart)
                        call.respond(HttpStatusCode.OK, mapOf("bagId" to id))
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }
                }


                post("/{tenantId}/bags/withImages"){
                    val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                    val multipart = call.receiveMultipart()
                    var bagDto: BagDto? = null
                    val fileParts = mutableListOf<PartData.FileItem>()

                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                if(part.name =="data"){
                                    bagDto = Json.decodeFromString<BagDto>(part.value)
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

                    if (bagDto == null) return@post call.respond(HttpStatusCode.BadRequest, "Bag information were not provided")

                    if(bagDto!!.marketingName == null) return@post call.respond(HttpStatusCode.BadRequest, "Bag requires property 'marketingName'")


                    //if(bagDto!!.userId == null) return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified")


                    val fileUploadResult = FileProcessing.handleFileUploads("$userId/bags",fileParts,allowedFileTypesString)
                    val uploadedImagesUrls = fileUploadResult["imageUrls"]?.toList()


                    bagDto = bagDto!!.copy(imageUrls =  uploadedImagesUrls, userId = userId)

                    val id = bagService.createOne(userId, bagDto!!)


                    if(fileUploadResult["fileExtensionNotAllowed"]!!.isNotEmpty()) call.respond(HttpStatusCode.Created, mapOf( "bagId" to id, "errors" to "File extensions not allowed : ${fileUploadResult["fileExtensionNotAllowed"]}"))
                    else call.respond(HttpStatusCode.Created, mapOf("bag" to bagDto!!.toEntity(id)))

                }

                get("/{tenantId}/bags/{id}") {

                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val bag = bagService.getOneById(userId, id)
                        if (bag != null) {
                            call.respond(bag)
                        }
                        call.respond(HttpStatusCode.NotFound)

                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

            get("/{tenantId}/bags") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val bag = bagService.getAll(userId)
                        if (bag.isEmpty()) {
                            call.respond("No bag found")
                        }
                        call.respond(HttpStatusCode.OK, bag)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                post("/{tenantId}/bags/withIds"){
                    try {
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val bagIds = call.receive<StringListRequest>()
                        val bags = bagService.getAllByIds(userId, bagIds.stringList)
                        if (bags.isEmpty()) {
                            call.respond("No bag found for those ids")
                        }
                        call.respond(HttpStatusCode.OK, bags)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }


                put("/{tenantId}/bags/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val bag = call.receive<BagDto>()
                        bagService.updateOneById(userId, id, bag).let {
                            val result =  if(it)  "Successfully modified bag with id $id"  else "bag with id $id not found"
                            call.respond(HttpStatusCode.OK,result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                delete("/{tenantId}/bags/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val imageUrls = call.receive<StringListRequest>()
                        bagService.deleteOneById(userId, id).let {
                            if(it) {
                                val filesDeleted = deleteUploadedFilesWithNames(imageUrls.stringList.toSet())
                                if(filesDeleted) call.respond(HttpStatusCode.OK,mapOf("bagId" to id))
                                else call.respond(HttpStatusCode.OK, mapOf("bagId" to id, "err" to "some images haven't been removed"))
                            }

                            else call.respond(HttpStatusCode.OK, "bag with id $id not found")

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