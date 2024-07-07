
import com.makassar.dto.UserDto
import com.makassar.entities.User
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class UserService(private val database: CoroutineDatabase) : CRUDService<UserDto,User> {
    private val userCollection = database.getCollection<User>()

    override suspend fun createOne(new: UserDto): String = withContext(Dispatchers.IO) {

        val user = User(

            id = UUID.randomUUID().toString(),
            username = new.username,
            email = new.email,
            passwordHash = new.password,
            roles = new.roles,
            createdAt = System.currentTimeMillis(),

        )

        userCollection.insertOne(user)
        user.id
    }

    override suspend fun getAll(): List<User> = withContext(Dispatchers.IO) {
        userCollection.find().toList()
    }


    override suspend fun getOneById(id: String): User? = withContext(Dispatchers.IO) {
        val user = userCollection.findOneById(id)
        user
    }

    override suspend fun updateOneById(id: String, updated: UserDto): Boolean = withContext(Dispatchers.IO) {
        val existingUser = userCollection.findOneById(id)
        if (existingUser != null) {
            val updatedUser = existingUser.copy(
                username = updated.username ?: existingUser.username,
                email = updated.email ?: existingUser.email,
                passwordHash = updated.password ?: existingUser.passwordHash,
                updatedAt = System.currentTimeMillis(),

            )

            val result = userCollection.replaceOneById(id, updatedUser)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = userCollection.deleteOneById(id)
        result.wasAcknowledged()
    }


}

