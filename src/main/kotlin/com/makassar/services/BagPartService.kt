
import com.makassar.dto.BagPartDto
import com.makassar.entities.BagPart
import com.makassar.services.GenericService
import com.makassar.utils.ServiceUtils
import com.mongodb.client.model.Filters.and
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.UUID


class BagPartService(private val database: CoroutineDatabase) : GenericService<BagPartDto,BagPart> {
    private val bagPartsCollection = database.getCollection<BagPart>()

    override suspend fun createOne(new: BagPartDto): String = withContext(Dispatchers.IO) {
        val bagItem = BagPart(
            id = UUID.randomUUID().toString(),
            userId = new.userId,
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

        bagPartsCollection.insertOne(bagItem)
        bagItem.id
    }

    override suspend fun getAll(userId : String): List<BagPart> = withContext(Dispatchers.IO) {
        bagPartsCollection.find(BagPart::userId eq userId).toList()
    }


    override suspend fun getOneById(userId: String, id: String): BagPart? = withContext(Dispatchers.IO) {
        val bagItem = bagPartsCollection.findOneById(and(BagPart::userId eq userId, BagPart::id eq id))
        bagItem
    }

    override suspend fun updateOneById(userId: String, id: String, updated: BagPartDto): Boolean = withContext(Dispatchers.IO) {
        val existingBagItem = bagPartsCollection.findOne(and(BagPart::id eq id, BagPart::userId eq userId))

        if (existingBagItem != null) {
            val updatedBagItem = existingBagItem.copy(
                marketingName = updated.marketingName ?: existingBagItem.marketingName,
                singleItemPrice = updated.singleItemPrice?: existingBagItem.singleItemPrice,
                ref = updated.ref ?: existingBagItem.ref,
                family = updated.family ?: existingBagItem.family,
                colors = updated.colors ?: existingBagItem.colors,
                measurements = updated.measurements ?: existingBagItem.measurements,
                size = updated.size ?: existingBagItem.size,
                materials = ServiceUtils.mergeMapsByReplacingQuantity(existingBagItem.materials,updated.materials),
                imageUrls = updated.imageUrls?: existingBagItem.imageUrls,
                description = updated.description?: existingBagItem.description,
                updatedAt = System.currentTimeMillis(),

            )
            val result = bagPartsCollection.replaceOneById(id, updatedBagItem)
            result.wasAcknowledged()
        } else {
            false
        }
    }


    override suspend fun deleteOneById(userId: String, id: String): Boolean = withContext(Dispatchers.IO) {
        val result = bagPartsCollection.deleteOneById(and(BagPart::userId eq userId, BagPart::id eq id))
        result.wasAcknowledged()
    }



}

