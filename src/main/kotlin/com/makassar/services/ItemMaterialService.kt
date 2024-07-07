
import com.makassar.dto.ItemMaterialDto
import com.makassar.entities.ItemMaterial
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class ItemMaterialService(private val database: CoroutineDatabase) : CRUDService<ItemMaterialDto,ItemMaterial> {
    private val productMaterialCollection = database.getCollection<ItemMaterial>()

    override suspend fun createOne(new: ItemMaterialDto): String = withContext(Dispatchers.IO) {
        val productMaterial = ItemMaterial(
            id = UUID.randomUUID().toString(),
            name = new.name,
            materialType = new.materialType,
            color = new.color,
            measurements = new.measurements,
            surface = new.surface,
            description = new.description,
            createdAt = System.currentTimeMillis(),

        )

        productMaterialCollection.insertOne(productMaterial)
        productMaterial.id
    }

    override suspend fun getAll(): List<ItemMaterial> = withContext(Dispatchers.IO) {
        productMaterialCollection.find().toList()
    }


    override suspend fun getOneById(id: String): ItemMaterial? = withContext(Dispatchers.IO) {
        val productMaterial = productMaterialCollection.findOneById(id)
        productMaterial
    }

    override suspend fun updateOneById(id: String, updated: ItemMaterialDto): Boolean = withContext(Dispatchers.IO) {
        val existingProductMaterial = productMaterialCollection.findOneById(id)
        if (existingProductMaterial != null) {
            val updatedProductMaterial = existingProductMaterial.copy(
                name = updated.name ?: existingProductMaterial.name,
                materialType = updated.materialType ?: existingProductMaterial.materialType,
                color = updated.color ?: existingProductMaterial.color,
                measurements = updated.measurements ?: existingProductMaterial.measurements,
                surface = updated.surface ?: existingProductMaterial.surface,
                description = updated.description?: existingProductMaterial.description,
                updatedAt = System.currentTimeMillis(),

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

