
import com.makassar.data.Order
import com.makassar.plugins.connectToMongoDB
import com.mongodb.client.MongoDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.ordersRoutes(
    db : MongoDatabase
) {
    val orderService = OrderService(db)
    routing {
        
        post("/orders") {
            val order = call.receive<Order>()
            val id = orderService.create(order)
            call.respond(HttpStatusCode.Created, id)
        }
        
        get("/orders/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
            orderService.read(id)?.let { order ->
                call.respond(order)
            } ?: call.respond(HttpStatusCode.NotFound)
        }
        
        put("/orders/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
            val order = call.receive<Order>()
            orderService.update(id, order)?.let {
                call.respond(HttpStatusCode.OK)
            } ?: call.respond(HttpStatusCode.NotFound)
        }
        
        delete("/orders/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("No ID found")
            orderService.delete(id)?.let {
                call.respond(HttpStatusCode.OK)
            } ?: call.respond(HttpStatusCode.NotFound)
        }
    }
}