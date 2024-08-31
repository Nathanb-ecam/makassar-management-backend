
import com.makassar.dto.CustomerDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.customersRoutes(
    customerService : CustomerService
) {

    routing {
        authenticate("access-jwt") {
            route("/api/customers") {
                post {
                    try {
                        val customer = call.receive<CustomerDto>()
                        if(customer.name != null){
                            val newCustomer = customerService.createOne(customer)
                            call.respond(HttpStatusCode.Created, mapOf("id" to newCustomer))
                        }else call.respond(HttpStatusCode.BadRequest,"Customer must have a name.")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val customer = customerService.getOneById(id)
                        if (customer != null) {
                            call.respond(customer)
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
                        val customers = customerService.getAll()
                        if (customers.isEmpty()) {
                            call.respond("No customers found")
                        }
                        call.respond(HttpStatusCode.OK, customers)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val customer = call.receive<CustomerDto>()
                        customerService.updateOneById(id, customer).let {
                            val result =  if(it)   mapOf("id" to id)  else mapOf("err" to "Customer with id $id not found")
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
                        customerService.deleteOneById(id).let {
                            val result =  if(it)  mapOf( "id" to id)  else mapOf("err" to "Customer with id $id not found")
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