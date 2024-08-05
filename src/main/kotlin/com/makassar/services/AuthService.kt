
import com.makassar.dto.requests.LoginRequest
import com.makassar.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


class AuthService(private val database: CoroutineDatabase) {
    private val userCollection = database.getCollection<User>()


    suspend fun loginWithMail(loginRequest: LoginRequest): User? = withContext(Dispatchers.IO) {
        val user = userCollection.findOne(User::mail eq loginRequest.mail)
        if(user != null && user.passwordHash == loginRequest.password){
            return@withContext user
        }
        return@withContext null
    }








}

