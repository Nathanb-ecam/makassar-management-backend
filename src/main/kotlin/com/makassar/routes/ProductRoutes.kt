
import com.makassar.dto.ProductDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.productRoutes(
    productService : ProductService
) {

    routing {
        
        post("/product") {
            try {
                val product = call.receive<ProductDto>()
                if(product.name != null){
                    val id = productService.createOne(product)
                    call.respond(HttpStatusCode.Created, id)
                }else call.respond(HttpStatusCode.BadRequest,"product must have a name.")
            }
            catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest,e.toString())
            }

        }

        get("/product/{id}") {

            try {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                val product = productService.getOneById(id)
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

        get("/product") {
            try {
                val product = productService.getAll()
                if (product.isEmpty()) {
                    call.respond("No product found")
                }
                call.respond(HttpStatusCode.OK, product)
            } catch (e : Exception){
                call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
            }
        }
        
        put("/product/{id}") {
            try {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                val product = call.receive<ProductDto>()
                productService.updateOneById(id, product).let {
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
        
        delete("/product/{id}") {
            try {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                productService.deleteOneById(id).let {
                    val result =  if(it)  "Successfully deleted product with id $id"  else "product with id $id not found"
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