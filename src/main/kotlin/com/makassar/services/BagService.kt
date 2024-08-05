
import com.makassar.dto.BagDto
import com.makassar.entities.Bag
import com.makassar.services.GenericService
import com.makassar.utils.ServiceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.`in`
import java.util.UUID


class BagService(private val database: CoroutineDatabase) : GenericService<BagDto,Bag> {
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
            //materials needs to be computed by taking all the materials used for creating the sub bagItems
            //materials = new.materials,
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

    suspend fun getAllByIds(ids: List<String>): List<Bag> = withContext(Dispatchers.IO) {
        bagCollection.find(Bag::id `in` ids ).toList()
    }


    override suspend fun getOneById(id: String): Bag? = withContext(Dispatchers.IO) {
        val bag = bagCollection.findOneById(id)
        bag
    }

    override suspend fun updateOneById(id: String, updated: BagDto): Boolean = withContext(Dispatchers.IO) {
        val existingBag = bagCollection.findOneById(id)
        if (existingBag != null) {
            val updatedBag = existingBag.copy(
                marketingName = updated.marketingName ?: existingBag.marketingName,
                sku = updated.sku?: existingBag.sku,
                retailPrice = updated.retailPrice ?: existingBag.retailPrice,
                colors = updated.colors ?: existingBag.colors,

                handles = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.handles,updated.handles),
                bodies = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.bodies,updated.bodies),
                shoulderStraps = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.shoulderStraps,updated.shoulderStraps),
                figures = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.figures,updated.figures),
                liners = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.liners,updated.liners),
                screws = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.screws,updated.screws),
                others = ServiceUtils.mergeMapsByReplacingQuantity(existingBag.others,updated.others),
                
                //materials = updated.materials?: existingBag.materials,
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

