
import com.makassar.dto.OrderDto
import com.makassar.entities.Order
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class OrderService(private val database: CoroutineDatabase) : CRUDService<OrderDto,Order> {
    private val orderCollection = database.getCollection<Order>()

    override suspend fun createOne(new: OrderDto): String = withContext(Dispatchers.IO) {
        val order = Order(
            id = UUID.randomUUID().toString(),
            customerId = new.customerId,
            price = new.price,
            discount = new.discount,
            products = new.products,
            destination = new.destination,
            dueDate = new.dueDate
        )

        orderCollection.insertOne(order)
        order.id
    }

    override suspend fun getAll(): List<Order> = withContext(Dispatchers.IO) {
        orderCollection.find().toList()
    }


    override suspend fun getOneById(id: String): Order? = withContext(Dispatchers.IO) {
        val order = orderCollection.findOneById(id)
        order
    }

    override suspend fun updateOneById(id: String, updated: OrderDto): Boolean = withContext(Dispatchers.IO) {
        val existingOrder = orderCollection.findOneById(id)
        if (existingOrder != null) {
            val updatedOrder = existingOrder.copy(
                customerId = updated.customerId ?: existingOrder.customerId,
                price = updated.price ?: existingOrder.price,
                discount = updated.discount ?: existingOrder.discount,
                products = updated.products ?: existingOrder.products,
                destination = updated.destination ?: existingOrder.destination,
                dueDate = updated.dueDate ?: existingOrder.dueDate
            )
            val result = orderCollection.replaceOneById(id, updatedOrder)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = orderCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

