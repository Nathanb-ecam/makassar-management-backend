
import com.makassar.dto.ProductDto
import com.makassar.entities.Product
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class ProductService(private val database: CoroutineDatabase) : CRUDService<ProductDto,Product> {
    private val productCollection = database.getCollection<Product>()

    override suspend fun createOne(new: ProductDto): String = withContext(Dispatchers.IO) {
        val product = Product(
            id = UUID.randomUUID().toString(),
            name = new.name,
            ref = new.ref,
            materials = new.materials,
            imageUrl = new.imageUrl,
            description = new.description,

        )

        productCollection.insertOne(product)
        product.id
    }

    override suspend fun getAll(): List<Product> = withContext(Dispatchers.IO) {
        productCollection.find().toList()
    }


    override suspend fun getOneById(id: String): Product? = withContext(Dispatchers.IO) {
        val product = productCollection.findOneById(id)
        product
    }

    override suspend fun updateOneById(id: String, updated: ProductDto): Boolean = withContext(Dispatchers.IO) {
        val existingProduct = productCollection.findOneById(id)
        if (existingProduct != null) {
            val updatedProduct = existingProduct.copy(
                name = updated.name ?: existingProduct.name,
                ref = updated.ref?: existingProduct.ref,
                materials = updated.materials?: existingProduct.materials,
                imageUrl = updated.imageUrl?: existingProduct.imageUrl,
                description = updated.description?: existingProduct.description,

            )
            val result = productCollection.replaceOneById(id, updatedProduct)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = productCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

