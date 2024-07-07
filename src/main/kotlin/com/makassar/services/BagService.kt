
import com.makassar.dto.BagDto
import com.makassar.entities.Bag
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class BagService(private val database: CoroutineDatabase) : CRUDService<BagDto,Bag> {
    private val bagCollection = database.getCollection<Bag>()

    override suspend fun createOne(new: BagDto): String = withContext(Dispatchers.IO) {
        val bag = Bag(
            id = UUID.randomUUID().toString(),
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
            materials = new.materials,
            imageUrls = new.imageUrls,
            description = new.description,
            createdAt = System.currentTimeMillis(),

        )

        bagCollection.insertOne(bag)
        bag.id
    }

    override suspend fun getAll(): List<Bag> = withContext(Dispatchers.IO) {
        bagCollection.find().toList()
    }


    override suspend fun getOneById(id: String): Bag? = withContext(Dispatchers.IO) {
        val Bag = bagCollection.findOneById(id)
        Bag
    }

    override suspend fun updateOneById(id: String, updated: BagDto): Boolean = withContext(Dispatchers.IO) {
        val existingBag = bagCollection.findOneById(id)
        if (existingBag != null) {
            val updatedBag = existingBag.copy(
                marketingName = updated.marketingName ?: existingBag.marketingName,
                sku = updated.sku?: existingBag.sku,
                retailPrice = updated.retailPrice ?: existingBag.retailPrice,
                handles = updated.handles,
                bodies = updated.bodies,
                shoulderStraps = updated.shoulderStraps,
                figures = updated.figures,
                liners = updated.liners,
                screws = updated.screws,
                others = updated.others,
                
                materials = updated.materials?: existingBag.materials,
                imageUrls = updated.imageUrls?: existingBag.imageUrls,
                description = updated.description?: existingBag.description,
                updatedAt = System.currentTimeMillis(),

            )
            val result = bagCollection.replaceOneById(id, updatedBag)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = bagCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

