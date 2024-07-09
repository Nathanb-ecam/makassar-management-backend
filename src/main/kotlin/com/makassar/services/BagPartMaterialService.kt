
import com.makassar.dto.BagPartMaterialDto
import com.makassar.entities.BagPartMaterial
import com.makassar.services.GenericService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class BagPartMaterialService(private val database: CoroutineDatabase) : GenericService<BagPartMaterialDto,BagPartMaterial> {
    private val bagPartMaterialsCollection = database.getCollection<BagPartMaterial>()

    override suspend fun createOne(new: BagPartMaterialDto): String = withContext(Dispatchers.IO) {
        val productMaterial = BagPartMaterial(
            id = UUID.randomUUID().toString(),
            name = new.name,
            materialType = new.materialType,
            color = new.color,
            measurements = new.measurements,
            surface = new.surface,
            description = new.description,
            createdAt = System.currentTimeMillis(),

        )

        bagPartMaterialsCollection.insertOne(productMaterial)
        productMaterial.id
    }

    override suspend fun getAll(): List<BagPartMaterial> = withContext(Dispatchers.IO) {
        bagPartMaterialsCollection.find().toList()
    }


    override suspend fun getOneById(id: String): BagPartMaterial? = withContext(Dispatchers.IO) {
        val productMaterial = bagPartMaterialsCollection.findOneById(id)
        productMaterial
    }

    override suspend fun updateOneById(id: String, updated: BagPartMaterialDto): Boolean = withContext(Dispatchers.IO) {
        val existingProductMaterial = bagPartMaterialsCollection.findOneById(id)
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
            val result = bagPartMaterialsCollection.replaceOneById(id, updatedProductMaterial)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = bagPartMaterialsCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

