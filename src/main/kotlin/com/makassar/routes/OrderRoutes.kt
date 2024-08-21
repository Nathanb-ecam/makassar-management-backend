
import com.makassar.dto.BagDto
import com.makassar.dto.OrderDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.CoroutineDatabase

fun Application.ordersRoutes(
    orderService : OrderService
) {

    routing {
        authenticate("access-jwt") {
            route("/api/orders"){
                post {
                    try {
                        val order = call.receive<OrderDto>()
                        if(order.customerId != null){
                            val id = orderService.createOne(order)
                            call.respond(HttpStatusCode.Created, mapOf("orderId" to id) )
                        }else call.respond(HttpStatusCode.BadRequest,"Customer Id cannot be null")


                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest,e.toString())
                    }

                }

                get("{id}") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = orderService.getOneById(id)
                        if (order != null) {
                            return@get call.respond(order)
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
                        val orders = orderService.getAll()
                        if (orders.isEmpty()) {
                            call.respond("No orders found")
                        }
                        call.respond(HttpStatusCode.OK, orders)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                put("{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = call.receive<OrderDto>()
                        orderService.updateOneById(id, order).let {
                            val result =  if(it)  "Successfully modified order with id $id"  else "Order with id $id not found"
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
                        orderService.deleteOneById(id).let {
                            val result =  if(it)  "Successfully deleted order with id $id"  else "Order with id $id not found"
                            call.respond(HttpStatusCode.OK, result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }


                get("{orderId}/addBag/{bagId}/{quantity}") {
                    try {
                        val orderId = call.parameters["orderId"] ?: throw IllegalArgumentException("No ID found")
                        val bagId = call.parameters["bagId"] ?: throw IllegalArgumentException("No quantity found")
                        val quantity = call.parameters["quantity"] ?: throw IllegalArgumentException("No quantity found")
                        orderService.addBagToOrder(orderId,bagId,quantity).let {
                            val result =  if(it)  "Successfully modified order with id $orderId"  else "Order with id $orderId not found"
                            call.respond(HttpStatusCode.OK,result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

            }

        }


    }
}