
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
            route("/api"){
                post("/orders") {
                    try {
                        val order = call.receive<OrderDto>()
                        if(order.customerId != null){
                            val id = orderService.createOne(order)
                            call.respond(HttpStatusCode.Created, mapOf("id" to id) )
                        }else call.respond(HttpStatusCode.BadRequest,"Customer Id cannot be null")


                    }
                    catch (e : Exception){
                        call.respond(HttpStatusCode.BadRequest, mapOf("err" to e.toString()))
                    }

                }

                get("/orders/{id}") {

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

                get("/orders/{id}/with-bags-detailed") {

                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = orderService.getOrderWithBagsDetailed(id)
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

                get("/orders") {
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


                get("/orders-overviews") {
                    try {
                        val orders = orderService.getOverviewsOfOrders()
                        if (orders.isEmpty()) {
                            call.respond("No orders found")
                        }
                        call.respond(HttpStatusCode.OK, orders)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }
                get("/orders/{id}/customer-detailed"){
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = orderService.getOrderWithCustomerDetailed(id)
                            ?: return@get call.respond(HttpStatusCode.NotFound,"Order not found")

                        call.respond(HttpStatusCode.OK, order)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }
                }

                get("/orders/{id}/fully-detailed"){
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        val order = orderService.getOrderFullyDetailedById(id)
                            ?: return@get call.respond(HttpStatusCode.NotFound,"Order not found")

                        call.respond(HttpStatusCode.OK, order)
                    } catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }

                put("/orders/{id}") {
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

                delete("/orders/{id}") {
                    try {
                        val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
                        orderService.deleteOneById(id).let {
                            val result =  if(it)  mapOf("id" to id)  else mapOf("err" to "Order with id $id not found")
                            call.respond(HttpStatusCode.OK, result)
                        }
                    }catch (e : IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest,"Invalid ID format")
                    }catch (e : Exception){
                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error : ${e}")
                    }

                }


                get("/orders/{orderId}/add-bag/{bagId}/{quantity}") {
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