
import com.makassar.dto.ProductPartDto
import com.makassar.entities.ProductPart
import com.makassar.services.GenericService
import com.makassar.utils.ServiceUtils
import com.mongodb.client.model.Filters.and
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.UUID


class ProductPartService(private val database: CoroutineDatabase) : GenericService<ProductPartDto,ProductPart> {
    private val productPartsCollection = database.getCollection<ProductPart>()

    override suspend fun createOne(userId: String, new: ProductPartDto): String = withContext(Dispatchers.IO) {
        val productItem = ProductPart(
            id = UUID.randomUUID().toString(),
            userId = userId,
            marketingName = new.marketingName,
            singleItemPrice = new.singleItemPrice,
            ref = new.ref,
            family = new.family,
            colors = new.colors,
            measurements = new.measurements,
            size = new.size,
            materials = new.materials,
            imageUrls = new.imageUrls,
            description = new.description,
            createdAt = System.currentTimeMillis(),

        )

        productPartsCollection.insertOne(productItem)
        productItem.id
    }

    override suspend fun getAll(userId : String): List<ProductPart> = withContext(Dispatchers.IO) {
        productPartsCollection.find(ProductPart::userId eq userId).toList()
    }


    override suspend fun getOneById(userId: String, id: String): ProductPart? = withContext(Dispatchers.IO) {
        val productItem = productPartsCollection.findOneById(and(ProductPart::userId eq userId, ProductPart::id eq id))
        productItem
    }

    override suspend fun updateOneById(userId: String, id: String, updated: ProductPartDto): Boolean = withContext(Dispatchers.IO) {
        val existingproductItem = productPartsCollection.findOne(and(ProductPart::id eq id, ProductPart::userId eq userId))

        if (existingproductItem != null) {
            val updatedproductItem = existingproductItem.copy(
                marketingName = updated.marketingName ?: existingproductItem.marketingName,
                singleItemPrice = updated.singleItemPrice?: existingproductItem.singleItemPrice,
                ref = updated.ref ?: existingproductItem.ref,
                family = updated.family ?: existingproductItem.family,
                colors = updated.colors ?: existingproductItem.colors,
                measurements = updated.measurements ?: existingproductItem.measurements,
                size = updated.size ?: existingproductItem.size,
                materials = ServiceUtils.mergeMapsByReplacingQuantity(existingproductItem.materials,updated.materials),
                imageUrls = updated.imageUrls?: existingproductItem.imageUrls,
                description = updated.description?: existingproductItem.description,
                updatedAt = System.currentTimeMillis(),

            )
            val result = productPartsCollection.replaceOneById(id, updatedproductItem)
            result.wasAcknowledged()
        } else {
            false
        }
    }


    override suspend fun deleteOneById(userId: String, id: String): Boolean = withContext(Dispatchers.IO) {
        val result = productPartsCollection.deleteOne(and(ProductPart::userId eq userId, ProductPart::id eq id))
        result.wasAcknowledged()
    }



}

