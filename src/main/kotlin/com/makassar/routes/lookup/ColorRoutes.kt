

import com.makassar.dto.lookup.ColorDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.colorRoutes(
    colorService : ColorService
) {

    routing {
        authenticate("admin-jwt"){
            route("/api/colors"){
                post {
                    try {
                        val color = call.receive<ColorDto>()
                        if(color.name != null){
                            val id = colorService.createOne(color)
                            call.respond(HttpStatusCode.Created, id)
                        }else call.respond(HttpStatusCode.BadRequest,"color must have a name.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val color = colorService.getOneById(id)
                        if (color != null) {
                            call.respond(color)
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
                        val color = colorService.getAll()
                        if (color.isEmpty()) {
                            call.respond("No color found")
                        }
                        call.respond(HttpStatusCode.OK, color)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val color = call.receive<ColorDto>()
                        colorService.updateOneById(id, color).let {
                            val result =  if(it)  "Successfully modified color with id $id"  else "color with id $id not found"
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
                        colorService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted color with id $id"  else "color with id $id not found"
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