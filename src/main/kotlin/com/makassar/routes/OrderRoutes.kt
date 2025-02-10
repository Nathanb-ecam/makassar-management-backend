
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
            route("/api"){
                post("/{tenantId}/orders") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@post call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val order = call.receive<OrderDto>()
                        if(order.customerId == null) return@post call.respond(HttpStatusCode.BadRequest, "Customer Id was not specified!")


                        val id = orderService.createOne(userId, order)
                        call.respond(HttpStatusCode.Created, mapOf("id" to id) )
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest, mapOf("err" to e.toString()))
                    }

                }

                get("/{tenantId}/orders/{id}") {

                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("order ID not found")

                        val order = orderService.getOneById(userId, id)
                        if (order != null) return@get call.respond(order)

                        call.respond(HttpStatusCode.NotFound)

                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                get("/{tenantId}/orders/{id}/with-products-detailed") {

                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("order ID not found")
                        val order = orderService.getOrderWithProductsDetailed(userId,id)
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

                get("/{tenantId}/orders") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val orders = orderService.getAll(userId)
                        if (orders.isEmpty()) {
                            call.respond("No orders found")
                        }
                        call.respond(HttpStatusCode.OK, orders)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }


                get("/{tenantId}/orders-overviews/{id}") {
                    try {
                        val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "userId not found")
                        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "order ID not found")
                        val order = orderService.getOrderOverviewById(userId,id) ?: return@get call.respond(HttpStatusCode.NotFound)

                        call.respond(HttpStatusCode.OK, mapOf("order" to order))
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                get("/{tenantId}/orders-overviews") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "userId not found")
                        val orders = orderService.getOverviewsOfOrders(userId)
                        if (orders.isEmpty()) {
                            call.respond("No orders found")
                        }
                        call.respond(HttpStatusCode.OK, orders)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }


                get("/{tenantId}/orders/{id}/customer-detailed"){
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = orderService.getOrderWithCustomerDetailed(userId, id)
                            ?: return@get call.respond(HttpStatusCode.NotFound,"Order not found")

                        call.respond(HttpStatusCode.OK, order)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                get("/{tenantId}/orders/{id}/fully-detailed"){
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = orderService.getOrderFullyDetailedById(userId, id)
                            ?: return@get call.respond(HttpStatusCode.NotFound,"Order not found")

                        call.respond(HttpStatusCode.OK, order)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                put("/{tenantId}/orders/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@put call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = call.receive<OrderDto>()
                        orderService.updateOneById(userId, id, order).let {
                            val result =  if(it)  mapOf("id" to id)  else mapOf("err" to "Order with id $id not found")
                            call.respond(HttpStatusCode.OK,result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                delete("/{tenantId}/orders/{id}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        orderService.deleteOneById(userId, id).let {
                            val result =  if(it)  mapOf("id" to id)  else mapOf("err" to "Order with id $id not found")
                            call.respond(HttpStatusCode.OK, result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }


                get("/{tenantId}/orders/{orderId}/add-product/{productId}/{quantity}") {
                    try {
                        val userId = call.parameters["tenantId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "tenantId was not specified!")
                        val orderId = call.parameters["orderId"] ?: throw IllegalArgumentException("orderId not found")
                        val productId = call.parameters["productId"] ?: throw IllegalArgumentException("No productId found")
                        val quantity = call.parameters["quantity"] ?: throw IllegalArgumentException("No quantity found")
                        orderService.addProductToOrder(userId, orderId,productId,quantity).let {
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