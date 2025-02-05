
import com.makassar.dto.UserDto
import com.makassar.dto.requests.LoginRequest
import com.makassar.entities.PendingUser
import com.makassar.entities.User
import com.makassar.utils.MailService
import com.makassar.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.*
import kotlin.random.Random


class AuthService(private val database: CoroutineDatabase) {
    private val userCollection = database.getCollection<User>()
    private val pendingUserCollection = database.getCollection<PendingUser>()


    suspend fun loginWithMail(loginRequest: LoginRequest): User? = withContext(Dispatchers.IO) {
        try {
            val user = userCollection.findOne(User::mail eq loginRequest.mail)
            if(user != null && PasswordUtils.verifyPassword(loginRequest.password, user.passwordHash!! )){
                return@withContext user
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }


    suspend fun createPendingUser(newUser : UserDto) : String = withContext(Dispatchers.IO) {

        val otp = Random.nextInt(100000,999999).toString()
        val user = PendingUser(
            id = UUID.randomUUID().toString(),
            otp = PasswordUtils.hashPassword(otp),
            username = newUser.username,
            mail = newUser.mail,
            passwordHash = newUser.password?.let { PasswordUtils.hashPassword(it) },
            roles = newUser.roles,
            createdAt = System.currentTimeMillis()
        )

        pendingUserCollection.insertOne(user)
        MailService.sendOtpEmail(user.mail!!, otp)
        return@withContext user.id
    }


    suspend fun createUserFromPendingUser(mail : String, oneTimePassword : String) : Boolean = withContext(Dispatchers.IO) {
        val pendingUser = pendingUserCollection.findOne(PendingUser::mail eq mail) ?: return@withContext false
        if(!(PasswordUtils.verifyPassword(oneTimePassword, pendingUser.otp))) return@withContext false
        //if(pendingUser.createdAt < System.currentTimeMillis()) return@withContext false

        val user = User(
            username = pendingUser.username,
            mail = pendingUser.mail,
            passwordHash = pendingUser.passwordHash,
            roles = pendingUser.roles,
            createdAt = pendingUser.createdAt
        )
        userCollection.insertOne(user)
        return@withContext true
    }










}

