
import com.makassar.dto.BagItemDto
import com.makassar.entities.BagItem
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class BagItemService(private val database: CoroutineDatabase) : CRUDService<BagItemDto,BagItem> {
    private val bagItemCollection = database.getCollection<BagItem>()

    override suspend fun createOne(new: BagItemDto): String = withContext(Dispatchers.IO) {
        val bagItem = BagItem(
            id = UUID.randomUUID().toString(),
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

        bagItemCollection.insertOne(bagItem)
        bagItem.id
    }

    override suspend fun getAll(): List<BagItem> = withContext(Dispatchers.IO) {
        bagItemCollection.find().toList()
    }


    override suspend fun getOneById(id: String): BagItem? = withContext(Dispatchers.IO) {
        val bagItem = bagItemCollection.findOneById(id)
        bagItem
    }

    override suspend fun updateOneById(id: String, updated: BagItemDto): Boolean = withContext(Dispatchers.IO) {
        val existingBagItem = bagItemCollection.findOneById(id)

        if (existingBagItem != null) {
            val updatedBagItem = existingBagItem.copy(
                marketingName = updated.marketingName ?: existingBagItem.marketingName,
                singleItemPrice = updated.singleItemPrice?: existingBagItem.singleItemPrice,
                ref = updated.ref ?: existingBagItem.ref,
                family = updated.family ?: existingBagItem.family,
                colors = updated.colors ?: existingBagItem.colors,
                measurements = updated.measurements ?: existingBagItem.measurements,
                size = updated.size ?: existingBagItem.size,
                materials = updated.materials?: existingBagItem.materials,
                imageUrls = updated.imageUrls?: existingBagItem.imageUrls,
                description = updated.description?: existingBagItem.description,
                updatedAt = System.currentTimeMillis(),

            )
            val result = bagItemCollection.replaceOneById(id, updatedBagItem)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = bagItemCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

