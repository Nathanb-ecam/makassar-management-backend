
import com.makassar.auth.JWTConfig
import com.makassar.dto.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.usersRoutes(
    userService : UserService
) {

    routing {

        authenticate("admin-jwt") {
            route("/api/users"){
                post {
                    try {
                        val user = call.receive<UserDto>()
                        if(user.mail != null){
                            val id = userService.createOne(user)
                            call.respond(HttpStatusCode.Created, id)
                        }else call.respond(HttpStatusCode.BadRequest,"User must have an email.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val user = userService.getOneById(id)
                        if (user != null) {
                            call.respond(user)
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
                        val users = userService.getAll()
                        if (users.isEmpty()) {
                            call.respond("No users found")
                        }
                        call.respond(HttpStatusCode.OK, users)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val user = call.receive<UserDto>()
                        userService.updateOneById(id, user).let {
                            val result =  if(it)  "Successfully modified user with id $id"  else "User with id $id not found"
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
                        userService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted user with id $id"  else "User with id $id not found"
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