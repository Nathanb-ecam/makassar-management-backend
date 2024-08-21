
import com.makassar.dto.BagDto
import com.makassar.dto.OrderDto
import com.makassar.entities.Order
import com.makassar.services.GenericService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class OrderService(private val database: CoroutineDatabase) : GenericService<OrderDto,Order> {
    private val orderCollection = database.getCollection<Order>()

    override suspend fun createOne(new: OrderDto): String = withContext(Dispatchers.IO) {
        val order = Order(
            id = UUID.randomUUID().toString(),
            customerId = new.customerId,
            status = new.status,
            totalPrice = new.totalPrice,
            deliveryCost = new.deliveryCost,
            discount = new.discount,
            bags = new.bags,
            plannedDate = new.plannedDate,
            createdAt = System.currentTimeMillis(),
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
                totalPrice = updated.totalPrice ?: existingOrder.totalPrice,
                deliveryCost = updated.deliveryCost ?: existingOrder.deliveryCost,
                discount = updated.discount ?: existingOrder.discount,
                status = updated.status ?: existingOrder.status,
                bags = updated.bags ?: existingOrder.bags,
                plannedDate = updated.plannedDate ?: existingOrder.plannedDate,
                updatedAt = System.currentTimeMillis(),
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

    suspend fun addBagToOrder(orderId:String,bagId:String, quantity: String) : Boolean = withContext(Dispatchers.IO) {
        val existingOrder = orderCollection.findOneById(orderId) ?: return@withContext false


        val updatedBags = existingOrder.bags?.toMutableMap() ?: mutableMapOf()

        if (updatedBags.containsKey(bagId)) {
            val existingQuantity = updatedBags[bagId]
            val newQuantity = existingQuantity?.toInt()?.plus(quantity.toInt())
            updatedBags[bagId] =  newQuantity.toString()
        } else {
            updatedBags[bagId] = quantity
        }


        val updatedOrder = existingOrder.copy(
            bags = updatedBags,
            updatedAt = System.currentTimeMillis()
        )

        val result = orderCollection.replaceOneById(orderId, updatedOrder)
        return@withContext result.wasAcknowledged()

    }



}

