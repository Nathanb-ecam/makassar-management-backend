

import com.makassar.dto.lookup.MaterialTypeDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.materialTypeRoutes(
    materialTypeService : MaterialTypeService
) {



    routing {
        authenticate("access-jwt"){
            route("/api/material-types"){
                post {
                    try {
                        val materialType = call.receive<MaterialTypeDto>()
                        if(materialType.name != null){
                            val id = materialTypeService.createOne(materialType)
                            call.respond(HttpStatusCode.Created, id)
                        }else call.respond(HttpStatusCode.BadRequest,"material-types must have a name.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val materialType = materialTypeService.getOneById(id)
                        if (materialType != null) {
                            call.respond(materialType)
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
                        val materialType = materialTypeService.getAll()
                        if (materialType.isEmpty()) {
                            call.respond("No materialType found")
                        }
                        call.respond(HttpStatusCode.OK, materialType)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val materialType = call.receive<MaterialTypeDto>()
                        materialTypeService.updateOneById(id, materialType).let {
                            val result =  if(it)  "Successfully modified materialType with id $id"  else "materialType with id $id not found"
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
                        materialTypeService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted materialType with id $id"  else "materialType with id $id not found"
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