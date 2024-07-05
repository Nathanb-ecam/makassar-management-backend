
import com.makassar.dto.ProductMaterialDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.productMaterialsRoutes(
    productMaterialService : ProductMaterialService
) {

    routing {
        
        post("/productMaterials") {
            try {
                val productMaterial = call.receive<ProductMaterialDto>()
                if(productMaterial.name != null){
                    val id = productMaterialService.createOne(productMaterial)
                    call.respond(HttpStatusCode.Created, id)
                }else call.respond(HttpStatusCode.BadRequest,"productMaterial must have a name.")
            }
            catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest,e.toString())
            }

        }

        get("/productMaterials/{id}") {

            try {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                val productMaterial = productMaterialService.getOneById(id)
                if (productMaterial != null) {
                    call.respond(productMaterial)
                }
                call.respond(HttpStatusCode.NotFound)

            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
            }catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
            }
        }

        get("/productMaterials") {
            try {
                val productMaterials = productMaterialService.getAll()
                if (productMaterials.isEmpty()) {
                    call.respond("No productMaterials found")
                }
                call.respond(HttpStatusCode.OK, productMaterials)
            } catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
            }
        }
        
        put("/productMaterials/{id}") {
            try {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                val productMaterial = call.receive<ProductMaterialDto>()
                productMaterialService.updateOneById(id, productMaterial).let {
                    val result =  if(it)  "Successfully modified productMaterial with id $id"  else "productMaterial with id $id not found"
                    call.respond(HttpStatusCode.OK,result)
                }
            }catch (e : IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
            }
            catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
            }

        }
        
        delete("/productMaterials/{id}") {
            try {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                productMaterialService.deleteOneById(id).let {
                    val result =  if(it)  "Successfully deleted productMaterial with id $id"  else "productMaterial with id $id not found"
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