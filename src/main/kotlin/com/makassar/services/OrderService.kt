

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
    private val ProductCollection = database.getCollection<Product>()
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

    override suspend fun createOne(userId: String, new: OrderDto): String = withContext(Dispatchers.IO) {

        val currentOrderNumber = getNextOrder()
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            customerId = new.customerId,
            orderNumber = currentOrderNumber.toString(),
            createdLocation = new.createdLocation,
            description = new.description,
            comments = new.comments,
            status = new.status,
            price =  new.price,
            products = new.products,
            plannedDate = new.plannedDate,
            createdAt = System.currentTimeMillis(),
        )

        orderCollection.insertOne(order)
        order.id
    }

    override suspend fun getAll(userId : String): List<Order> = withContext(Dispatchers.IO) {
        orderCollection.find(User::id eq userId).toList()
    }


    suspend fun getOrderWithProductsDetailed(userId : String, orderId: String): OrderProductDetailed? {
        val order = orderCollection.findOne(and(Order::id eq orderId, Order::userId eq userId)) ?: return null

        val ProductIds = order.products?.keys?.toList() ?: emptyList()

        val Products = ProductCollection.find(Product::id `in` ProductIds).toList()

        val ProductsWithDetails = Products.associateWith { Product -> order.products?.get(Product.id) ?: "0" }

        val orderWithDetails = order.toProductDetailedOrder(ProductsWithDetails)

        return orderWithDetails
    }

    override suspend fun updateOneById(userId : String, orderId: String, updated: OrderDto): Boolean = withContext(Dispatchers.IO) {
        val existingOrder = orderCollection.findOne(and(Order::id eq orderId, Order::userId eq userId)) ?: return@withContext false

        val updatedOrder = existingOrder.copy(
            customerId = updated.customerId ?: existingOrder.customerId,
            createdLocation = updated.createdLocation ?: existingOrder.createdLocation,
            comments = updated.comments ?: existingOrder.comments,
            description = updated.description ?: existingOrder.description,
            price = updated.price ?: existingOrder.price,
            status = updated.status ?: existingOrder.status,
            products = updated.products ?: existingOrder.products,
            plannedDate = updated.plannedDate ?: existingOrder.plannedDate,
            updatedAt = System.currentTimeMillis(),
        )
        val result = orderCollection.replaceOne(
            and(Order::id eq orderId, Order::userId eq userId),
            updatedOrder
        )
        result.wasAcknowledged()


    }

    override suspend fun getOneById(userId : String, orderId: String): Order? = withContext(Dispatchers.IO) {
        val order = orderCollection.findOne(and(Order::id eq orderId, Order::userId eq userId))
        order
    }


    override suspend fun deleteOneById(userId : String, orderId: String): Boolean = withContext(Dispatchers.IO) {
        val result = orderCollection.deleteOneById(and(Order::userId eq userId,Order::id eq orderId))
        result.wasAcknowledged()
    }

    suspend fun addProductToOrder(userId: String,orderId: String, ProductId: String, quantity: String): Boolean = withContext(Dispatchers.IO) {
        val existingOrder = orderCollection.findOne(and(Order::userId eq userId, Order::id eq orderId)) ?: return@withContext false


        val updatedProducts = existingOrder.products?.toMutableMap() ?: mutableMapOf()

        if (updatedProducts.containsKey(ProductId)) {
            val existingQuantity = updatedProducts[ProductId]
            val newQuantity = existingQuantity?.toInt()?.plus(quantity.toInt())
            updatedProducts[ProductId] = newQuantity.toString()
        } else {
            updatedProducts[ProductId] = quantity
        }


        val updatedOrder = existingOrder.copy(
            products = updatedProducts,
            updatedAt = System.currentTimeMillis()
        )

        val result = orderCollection.replaceOne(and(Order::id eq orderId, Order::userId eq userId), updatedOrder)
        return@withContext result.wasAcknowledged()

    }


    suspend fun getOverviewsOfOrders(userId: String): List<OrderOverview> = withContext(Dispatchers.IO) {
        return@withContext orderCollection.aggregate<OrderOverview>(
            match(Order::userId eq userId),
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

    suspend fun getOrderOverviewById(userId : String, orderId : String) : OrderOverview? = withContext(Dispatchers.IO){
        return@withContext orderCollection.aggregate<OrderOverview>(
            match(and(Order::id eq orderId, Order::userId eq userId )),
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


    suspend fun getOrderWithCustomerDetailed(userId: String, orderId: String): OrderCustomerDetailed? = withContext(Dispatchers.IO) {
        return@withContext orderCollection.aggregate<OrderCustomerDetailed>(
            match(and(Order::id eq orderId, Order::userId eq userId )),
            lookup(from = "customer", localField = "customerId", foreignField = "_id", newAs = "customer"),
            unwind("\$customer"),
            project(
                OrderCustomerDetailed::id from "\$_id",
                OrderCustomerDetailed::customer from "\$customer",
                OrderCustomerDetailed::createdLocation from "\$createdLocation",
                OrderCustomerDetailed::description from "\$description",
                OrderCustomerDetailed::comments from "\$comments",
                OrderCustomerDetailed::price from "\$price",
                OrderCustomerDetailed::products from "\$products",
                OrderCustomerDetailed::plannedDate from "\$plannedDate",
            )
        ).toList().firstOrNull()

    }


    suspend fun getOrderFullyDetailedById(userId: String, orderId: String): OrderFullyDetailed? = withContext(Dispatchers.IO) {
        try {
            val orderCustomerDetailed = orderCollection.aggregate<OrderCustomerDetailed>(
                match(and(Order::id eq orderId, Order::userId eq userId )),
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
                    OrderCustomerDetailed::products from "\$products",
                    OrderCustomerDetailed::plannedDate from "\$plannedDate",
                )
            ).toList().firstOrNull() ?: throw NoSuchElementException("Order with $orderId not found")


            val ProductIds = orderCustomerDetailed.products?.keys?.toSet() ?: emptySet()

            val Products = ProductCollection.find(Product::id `in` ProductIds).toList()




            val map: Map<String, ProductWithQuantity> = Products.associateBy(
                { it.id },
                { Product -> ProductWithQuantity(Product, orderCustomerDetailed.products?.get(Product.id) ?: "0") }
            )

/*            val ProductsWithQuantities = Products.map { Product ->
                ProductWithQuantity(
                    Product = Product,
                    quantity = orderCustomerDetailed.Products?.get(Product.id) ?: "0"
                )
            }*/


            val orderWithDetails = orderCustomerDetailed.toFullyDetailed(
                products = map,
            )
            println(orderWithDetails)
            return@withContext orderWithDetails

        }catch (e : Exception){
            logger.error(e.toString())
            return@withContext null
        }

    }


}

