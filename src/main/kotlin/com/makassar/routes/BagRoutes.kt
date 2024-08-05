
import com.makassar.dto.BagDto
import com.makassar.dto.requests.IdsRequest
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
import org.slf4j.LoggerFactory

fun Application.bagRoutes(
    bagService : BagService
) {

    val allowedFileTypesString = environment.config.tryGetString("allowedUploadFileTypes") ?: "png,jpg,jpeg"
    val logger = LoggerFactory.getLogger("BagRoutes")
    routing {
        authenticate("access-jwt"){
            route("/api/bags"){
                post{
                    try{
                        val bagPart = call.receive<BagDto>()
                        if(bagPart.marketingName == null) return@post call.respond(HttpStatusCode.BadRequest,"Bag requires property 'marketingName'")

                        val id = bagService.createOne(bagPart)
                        call.respond(HttpStatusCode.OK, mapOf("bagId" to id))
                    }catch (e: Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }
                }


                post("/withImages"){
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
                        part.dispose()
                    }

                    if (bagDto == null) return@post call.respond(HttpStatusCode.BadRequest, "Bag information were not provided")

                    if(bagDto!!.marketingName == null){
                        return@post call.respond(HttpStatusCode.BadRequest, "Bag requires property 'marketingName'")
                    }

                    val fileUploadResult = FileUploadProcessing.handleFileUploads("bags",fileParts,allowedFileTypesString)
                    val uploadedImagesUrls = fileUploadResult["imageUrls"]?.toList()


                    bagDto = bagDto!!.copy(
                        imageUrls =  uploadedImagesUrls,
                    )

                    val id = bagService.createOne(bagDto!!)
                    if(fileUploadResult["fileExtensionNotAllowed"]!!.isNotEmpty()) call.respond(HttpStatusCode.Created, mapOf( "bagId" to id, "errors" to "File extensions not allowed : ${fileUploadResult["fileExtensionNotAllowed"]}"))
                    else call.respond(HttpStatusCode.Created, mapOf("bagId" to id))

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val bag = bagService.getOneById(id)
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

            get {
                    try {
                        val bag = bagService.getAll()
                        if (bag.isEmpty()) {
                            call.respond("No bag found")
                        }
                        call.respond(HttpStatusCode.OK, bag)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                post("/withIds"){
                    try {
                        val bagIds = call.receive<IdsRequest>()
                        val bags = bagService.getAllByIds(bagIds.ids)
                        if (bags.isEmpty()) {
                            call.respond("No bag found for those ids")
                        }
                        call.respond(HttpStatusCode.OK, bags)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }


                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val bag = call.receive<BagDto>()
                        bagService.updateOneById(id, bag).let {
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

                delete("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        bagService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted bag with id $id"  else "bag with id $id not found"
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