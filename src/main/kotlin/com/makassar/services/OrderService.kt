
import com.makassar.data.Order
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId

class OrderService(private val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("Orders")
        collection = database.getCollection("Orders")
    }


    suspend fun create(order: Order): String = withContext(Dispatchers.IO) {
        val doc = order.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }


    suspend fun read(id: String): Order? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(Order.Companion::fromDocument)
    }


    suspend fun update(id: String, order: Order): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), order.toDocument())
    }


    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}

