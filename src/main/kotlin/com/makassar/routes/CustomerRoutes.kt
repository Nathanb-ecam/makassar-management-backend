
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
            route("/api") {
                post("/{tenantId}/customers") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val customer = call.receive<CustomerDto>()

                        if(customer.name == null) return@post call.respond(HttpStatusCode.BadRequest, "Customer name was not specified!")


                        val newCustomer = customerService.createOne(userId, customer)
                        call.respond(HttpStatusCode.Created, mapOf("id" to newCustomer))

                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("/{tenantId}/customers/{id}") {

                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val customer = customerService.getOneById(userId, id)
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

                get("/{tenantId}/customers") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val customers = customerService.getAll(userId)
                        if (customers.isEmpty()) {
                            call.respond("No customers found")
                        }
                        call.respond(HttpStatusCode.OK, customers)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("/{tenantId}/customers/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val customer = call.receive<CustomerDto>()
                        customerService.updateOneById(userId, id, customer).let {
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

                delete("/{tenantId}/customers/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        customerService.deleteOneById(userId, id).let {
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