
import com.makassar.dto.ProductDto
import com.makassar.entities.Product
import com.makassar.services.GenericService
import com.makassar.utils.ServiceUtils
import com.mongodb.client.model.Filters.and
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import java.util.UUID


class ProductService(private val database: CoroutineDatabase) : GenericService<ProductDto,Product> {
    private val ProductCollection = database.getCollection<Product>()

    override suspend fun createOne(userId: String, new: ProductDto): String = withContext(Dispatchers.IO) {
        val Product = Product(
            id = UUID.randomUUID().toString(),
            userId = userId,
            marketingName = new.marketingName,
            retailPrice = new.retailPrice,
            sku = new.sku,
            handles = new.handles,
            bodies = new.bodies,
            shoulderStraps = new.shoulderStraps,
            figures = new.figures,
            liners = new.liners,
            screws = new.screws,
            others = new.others,
            //materials needs to be computed by taking all the materials used for creating the sub ProductItems
            //materials = new.materials,
            imageUrls = new.imageUrls,
            description = new.description,
            createdAt = System.currentTimeMillis(),

        )

        ProductCollection.insertOne(Product)
        Product.id
    }

    override suspend fun getAll(userId: String): List<Product> = withContext(Dispatchers.IO) {
        ProductCollection.find(Product::userId eq userId).toList()
    }

    suspend fun getAllByIds(userId: String, ids: List<String>): List<Product> = withContext(Dispatchers.IO) {
        ProductCollection.find(and(Product::userId eq userId,Product::id `in` ids) ).toList()
    }


    override suspend fun getOneById(userId : String, id: String): Product? = withContext(Dispatchers.IO) {
        val Product = ProductCollection.findOne(and(Product::userId eq userId, Product::id eq id))
        Product
    }

    override suspend fun updateOneById(userId: String, id: String, updated: ProductDto): Boolean = withContext(Dispatchers.IO) {
        val existingProduct = ProductCollection.findOne(and(Product::userId eq userId, Product::id eq id))
        if (existingProduct != null) {
            val updatedProduct = existingProduct.copy(
                marketingName = updated.marketingName ?: existingProduct.marketingName,
                sku = updated.sku?: existingProduct.sku,
                retailPrice = updated.retailPrice ?: existingProduct.retailPrice,
                colors = updated.colors ?: existingProduct.colors,

                handles = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.handles,updated.handles),
                bodies = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.bodies,updated.bodies),
                shoulderStraps = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.shoulderStraps,updated.shoulderStraps),
                figures = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.figures,updated.figures),
                liners = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.liners,updated.liners),
                screws = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.screws,updated.screws),
                others = ServiceUtils.mergeMapsByReplacingQuantity(existingProduct.others,updated.others),
                
                //materials = updated.materials?: existingProduct.materials,
                imageUrls = updated.imageUrls?: existingProduct.imageUrls,
                description = updated.description?: existingProduct.description,
                updatedAt = System.currentTimeMillis(),

            )
            val result = ProductCollection.replaceOneById(id, updatedProduct)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(userId : String, id: String): Boolean = withContext(Dispatchers.IO) {
        val result = ProductCollection.deleteOne(and(Product::userId eq userId, Product::id eq id))
        result.wasAcknowledged()
    }



}

