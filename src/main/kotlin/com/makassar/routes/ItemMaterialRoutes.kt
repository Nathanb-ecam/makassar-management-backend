
import com.makassar.dto.ItemMaterialDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.itemMaterialRoutes(
    itemMaterialService : ItemMaterialService
) {

    routing {
        authenticate("admin-jwt") {
            route("/api/item-materials") {
                post {
                    try {
                        val itemMaterial = call.receive<ItemMaterialDto>()
                        if(itemMaterial.name != null){
                            val id = itemMaterialService.createOne(itemMaterial)
                            call.respond(HttpStatusCode.Created, id)
                        }else call.respond(HttpStatusCode.BadRequest,"itemMaterial must have a name.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val itemMaterial = itemMaterialService.getOneById(id)
                        if (itemMaterial != null) {
                            call.respond(itemMaterial)
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
                        val itemMaterials = itemMaterialService.getAll()
                        if (itemMaterials.isEmpty()) {
                            call.respond("No itemMaterials found")
                        }
                        call.respond(HttpStatusCode.OK, itemMaterials)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val itemMaterial = call.receive<ItemMaterialDto>()
                        itemMaterialService.updateOneById(id, itemMaterial).let {
                            val result =  if(it)  "Successfully modified itemMaterial with id $id"  else "itemMaterial with id $id not found"
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
                        itemMaterialService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted itemMaterial with id $id"  else "itemMaterial with id $id not found"
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