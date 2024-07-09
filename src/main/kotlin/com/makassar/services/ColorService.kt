

import com.makassar.dto.lookup.ColorDto
import com.makassar.entities.lookup.Color
import com.makassar.services.GenericService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class ColorService(private val database: CoroutineDatabase) : GenericService<ColorDto, Color> {
    private val colorCollection = database.getCollection<Color>()

    override suspend fun createOne(new: ColorDto): String = withContext(Dispatchers.IO) {
        val color = Color(
            id = UUID.randomUUID().toString(),
            name = new.name,
            hexCode = new.hexCode,

        )

        colorCollection.insertOne(color)
        color.id
    }

    override suspend fun getAll(): List<Color> = withContext(Dispatchers.IO) {
        colorCollection.find().toList()
    }


    override suspend fun getOneById(id: String): Color? = withContext(Dispatchers.IO) {
        val color = colorCollection.findOneById(ObjectId(id))
        color
    }

    override suspend fun updateOneById(id: String, updated: ColorDto): Boolean = withContext(Dispatchers.IO) {
        val existingcolor = colorCollection.findOneById(id)

        if (existingcolor != null) {
            val updatedcolor = existingcolor.copy(
                name = updated.name ?: existingcolor.name,
                hexCode = updated.hexCode?: existingcolor.hexCode,
            )
            val result = colorCollection.replaceOneById(ObjectId(id), updatedcolor)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = colorCollection.deleteOneById(ObjectId(id))
        result.wasAcknowledged()
    }



}

