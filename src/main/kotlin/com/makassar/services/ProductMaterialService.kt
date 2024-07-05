
import com.makassar.dto.ProductMaterialDto
import com.makassar.entities.ProductMaterial
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class ProductMaterialService(private val database: CoroutineDatabase) : CRUDService<ProductMaterialDto,ProductMaterial> {
    private val productMaterialCollection = database.getCollection<ProductMaterial>()

    override suspend fun createOne(new: ProductMaterialDto): String = withContext(Dispatchers.IO) {
        val productMaterial = ProductMaterial(
            id = UUID.randomUUID().toString(),
            name = new.name,
            pricePerUnit= new.pricePerUnit,
            dimensions = new.dimensions,
            surface = new.surface,
            description = new.description,

        )

        productMaterialCollection.insertOne(productMaterial)
        productMaterial.id
    }

    override suspend fun getAll(): List<ProductMaterial> = withContext(Dispatchers.IO) {
        productMaterialCollection.find().toList()
    }


    override suspend fun getOneById(id: String): ProductMaterial? = withContext(Dispatchers.IO) {
        val productMaterial = productMaterialCollection.findOneById(id)
        productMaterial
    }

    override suspend fun updateOneById(id: String, updated: ProductMaterialDto): Boolean = withContext(Dispatchers.IO) {
        val existingProductMaterial = productMaterialCollection.findOneById(id)
        if (existingProductMaterial != null) {
            val updatedProductMaterial = existingProductMaterial.copy(
                name = updated.name ?: existingProductMaterial.name,
                pricePerUnit= updated.pricePerUnit ?: existingProductMaterial.pricePerUnit,
                dimensions = updated.dimensions ?: existingProductMaterial.dimensions,
                surface = updated.surface ?: existingProductMaterial.surface,
                description = updated.description?: existingProductMaterial.description,

            )
            val result = productMaterialCollection.replaceOneById(id, updatedProductMaterial)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = productMaterialCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

