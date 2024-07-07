
import com.makassar.dto.BagItemDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.bagItemRoutes(
    bagItemService : BagItemService
) {



    routing {
        authenticate("admin-jwt"){
            route("/api/bag-items"){
                post {
                    try {
                        val BagItem = call.receive<BagItemDto>()
                        if(BagItem.marketingName != null){
                            val id = bagItemService.createOne(BagItem)
                            call.respond(HttpStatusCode.Created, id)
                        }else call.respond(HttpStatusCode.BadRequest,"BagItem must have a name.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val BagItem = bagItemService.getOneById(id)
                        if (BagItem != null) {
                            call.respond(BagItem)
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
                        val BagItem = bagItemService.getAll()
                        if (BagItem.isEmpty()) {
                            call.respond("No BagItem found")
                        }
                        call.respond(HttpStatusCode.OK, BagItem)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val BagItem = call.receive<BagItemDto>()
                        bagItemService.updateOneById(id, BagItem).let {
                            val result =  if(it)  "Successfully modified BagItem with id $id"  else "BagItem with id $id not found"
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
                        bagItemService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted BagItem with id $id"  else "BagItem with id $id not found"
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