
import com.makassar.dto.BagDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.bagRoutes(
    bagService : BagService
) {



    routing {
        authenticate("admin-jwt"){
            route("/api/bags"){
                post {
                    try {
                        val bag = call.receive<BagDto>()
                        if(bag.marketingName != null){
                            val id = bagService.createOne(bag)
                            call.respond(HttpStatusCode.Created, id)
                        }else call.respond(HttpStatusCode.BadRequest,"bag must have a name.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

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