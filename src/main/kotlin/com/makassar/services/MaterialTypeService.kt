

import com.makassar.dto.lookup.MaterialTypeDto
import com.makassar.entities.lookup.MaterialType
import com.makassar.services.GenericService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class MaterialTypeService(private val database: CoroutineDatabase) : GenericService<MaterialTypeDto, MaterialType> {
    private val materialTypeCollection = database.getCollection<MaterialType>()

    override suspend fun createOne(new: MaterialTypeDto): String = withContext(Dispatchers.IO) {
        val materialType = MaterialType(
            id = UUID.randomUUID().toString(),
            name = new.name,
            description = new.description,
        )

        materialTypeCollection.insertOne(materialType)
        materialType.id
    }

    override suspend fun getAll(): List<MaterialType> = withContext(Dispatchers.IO) {
        materialTypeCollection.find().toList()
    }


    override suspend fun getOneById(id: String): MaterialType? = withContext(Dispatchers.IO) {
        val materialType = materialTypeCollection.findOneById(id)
        materialType
    }

    override suspend fun updateOneById(id: String, updated: MaterialTypeDto): Boolean = withContext(Dispatchers.IO) {
        val existingmaterialType = materialTypeCollection.findOneById(id)

        if (existingmaterialType != null) {
            val updatedmaterialType = existingmaterialType.copy(
                name = updated.name ?: existingmaterialType.name,
                description = updated.description?: existingmaterialType.description,
            )
            val result = materialTypeCollection.replaceOneById(id, updatedmaterialType)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = materialTypeCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

