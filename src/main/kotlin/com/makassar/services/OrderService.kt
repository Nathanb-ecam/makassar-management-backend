

import com.makassar.dto.*
import com.makassar.entities.*
import com.makassar.entities.countersLookup.Sequence
import com.makassar.services.GenericService
import com.mongodb.client.model.Aggregates.unwind
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.slf4j.LoggerFactory
import java.util.UUID


class OrderService(private val database: CoroutineDatabase) : GenericService<OrderDto,Order> {
    private val orderCollection = database.getCollection<Order>()
    private val bagCollection = database.getCollection<Bag>()
    private val customerCollection = database.getCollection<Customer>()
    private val sequenceCollection = database.getCollection<Sequence>()

    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    suspend fun getNextOrder(): Long {

        val updatedSequence = sequenceCollection.findOneAndUpdate(
            Filters.eq("_id", "order"),
            Updates.inc("sequenceValue", 1),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER).upsert(true)
        )
        return updatedSequence?.sequenceValue ?: 1
    }

    override suspend fun createOne(new: OrderDto): String = withContext(Dispatchers.IO) {

        val currentOrderNumber = getNextOrder()
        val order = Order(
            id = UUID.randomUUID().toString(),
            customerId = new.customerId,
            orderNumber = currentOrderNumber.toString(),
            createdLocation = new.createdLocation,
            description = new.description,
            comments = new.comments,
            status = new.status,
            price =  new.price,
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


    suspend fun getOrderWithBagsDetailed(orderId: String): OrderBagDetailed? {

        val order = orderCollection.findOneById(orderId) ?: return null

        val bagIds = order.bags?.keys?.toList() ?: emptyList()

        val bags = bagCollection.find(Bag::id `in` bagIds).toList()

        val bagsWithDetails = bags.associateWith { bag -> order.bags?.get(bag.id) ?: "0" }

        val orderWithDetails = order.toBagDetailedOrder(bagsWithDetails)

        return orderWithDetails
    }

    override suspend fun updateOneById(id: String, updated: OrderDto): Boolean = withContext(Dispatchers.IO) {
        val existingOrder = orderCollection.findOneById(id) ?: return@withContext false

        val updatedOrder = existingOrder.copy(
            customerId = updated.customerId ?: existingOrder.customerId,
            createdLocation = updated.createdLocation ?: existingOrder.createdLocation,
            comments = updated.comments ?: existingOrder.comments,
            description = updated.description ?: existingOrder.description,
            price = updated.price ?: existingOrder.price,
            status = updated.status ?: existingOrder.status,
            bags = updated.bags ?: existingOrder.bags,
            plannedDate = updated.plannedDate ?: existingOrder.plannedDate,
            updatedAt = System.currentTimeMillis(),
        )
        val result = orderCollection.replaceOneById(id, updatedOrder)
        result.wasAcknowledged()

    }

    override suspend fun getOneById(id: String): Order? = withContext(Dispatchers.IO) {
        val order = orderCollection.findOneById(id)
        order
    }


    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = orderCollection.deleteOneById(id)
        result.wasAcknowledged()
    }

    suspend fun addBagToOrder(orderId: String, bagId: String, quantity: String): Boolean = withContext(Dispatchers.IO) {
        val existingOrder = orderCollection.findOneById(orderId) ?: return@withContext false


        val updatedBags = existingOrder.bags?.toMutableMap() ?: mutableMapOf()

        if (updatedBags.containsKey(bagId)) {
            val existingQuantity = updatedBags[bagId]
            val newQuantity = existingQuantity?.toInt()?.plus(quantity.toInt())
            updatedBags[bagId] = newQuantity.toString()
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


    suspend fun getOverviewsOfOrders(): List<OrderOverview> = withContext(Dispatchers.IO) {
        return@withContext orderCollection.aggregate<OrderOverview>(
            lookup(from = "customer", localField = "customerId", foreignField = "_id", newAs = "customer"),
            unwind("\$customer"),
            project(
                OrderOverview::id from "\$_id",
                OrderOverview::customerName from "\$customer.name",
                OrderOverview::orderNumber from "\$orderNumber",
                OrderOverview::status from "\$status",
                OrderOverview::price from "\$price",
                OrderOverview::plannedDate from "\$plannedDate",
                OrderOverview::createdAt from "\$createdAt",
                OrderOverview::updatedAt from "\$updatedAt",
            )
        ).toList()
    }

    suspend fun getOrderOverviewById(id : String) : OrderOverview? = withContext(Dispatchers.IO){
        return@withContext orderCollection.aggregate<OrderOverview>(
            match(Order::id eq id ),
            lookup(from = "customer", localField = "customerId", foreignField = "_id", newAs = "customer"),
            unwind("\$customer"),
            project(
                OrderOverview::id from "\$_id",
                OrderOverview::customerName from "\$customer.name",
                OrderOverview::orderNumber from "\$orderNumber",
                OrderOverview::status from "\$status",
                OrderOverview::price from "\$price",
                OrderOverview::plannedDate from "\$plannedDate",
                OrderOverview::createdAt from "\$createdAt",
                OrderOverview::updatedAt from "\$updatedAt",
            )
        ).first()
    }


    suspend fun getOrderWithCustomerDetailed(id: String): OrderCustomerDetailed? = withContext(Dispatchers.IO) {
        return@withContext orderCollection.aggregate<OrderCustomerDetailed>(
            match(Order::id eq id),
            lookup(from = "customer", localField = "customerId", foreignField = "_id", newAs = "customer"),
            unwind("\$customer"),
            project(
                OrderCustomerDetailed::id from "\$_id",
                OrderCustomerDetailed::customer from "\$customer",
                OrderCustomerDetailed::createdLocation from "\$createdLocation",
                OrderCustomerDetailed::description from "\$description",
                OrderCustomerDetailed::comments from "\$comments",
                OrderCustomerDetailed::price from "\$price",
                OrderCustomerDetailed::bags from "\$bags",
                OrderCustomerDetailed::plannedDate from "\$plannedDate",
            )
        ).toList().firstOrNull()

    }


    suspend fun getOrderFullyDetailedById(id: String): OrderFullyDetailed? = withContext(Dispatchers.IO) {
        try {
            val orderCustomerDetailed = orderCollection.aggregate<OrderCustomerDetailed>(
                match(Order::id eq id),
                lookup(from = "customer", localField = "customerId", foreignField = "_id", newAs = "customer"),
                unwind("\$customer"),
                project(
                    OrderCustomerDetailed::id from "\$_id",
                    OrderCustomerDetailed::customer from "\$customer",
                    OrderCustomerDetailed::createdLocation from "\$createdLocation",
                    OrderCustomerDetailed::description from "\$description",
                    OrderCustomerDetailed::comments from "\$comments",
                    OrderCustomerDetailed::status from "\$status",
                    OrderCustomerDetailed::price from "\$price",
                    OrderCustomerDetailed::bags from "\$bags",
                    OrderCustomerDetailed::plannedDate from "\$plannedDate",
                )
            ).toList().firstOrNull() ?: throw NoSuchElementException("Order with $id not found")


            val bagIds = orderCustomerDetailed.bags?.keys?.toSet() ?: emptySet()

            val bags = bagCollection.find(Bag::id `in` bagIds).toList()




            val map: Map<String, BagWithQuantity> = bags.associateBy(
                { it.id },
                { bag -> BagWithQuantity(bag, orderCustomerDetailed.bags?.get(bag.id) ?: "0") }
            )

/*            val bagsWithQuantities = bags.map { bag ->
                BagWithQuantity(
                    bag = bag,
                    quantity = orderCustomerDetailed.bags?.get(bag.id) ?: "0"
                )
            }*/


            val orderWithDetails = orderCustomerDetailed.toFullyDetailed(
                bags = map,
            )
            println(orderWithDetails)
            return@withContext orderWithDetails

        }catch (e : Exception){
            logger.error(e.toString())
            return@withContext null
        }

    }


}

