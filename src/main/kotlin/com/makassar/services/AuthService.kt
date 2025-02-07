
import com.makassar.dto.UserDto
import com.makassar.dto.requests.LoginRequest
import com.makassar.entities.PendingUser
import com.makassar.entities.User
import com.makassar.utils.MailService
import com.makassar.utils.SecurityUtils
import com.makassar.utils.ServiceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.MongoOperator
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
            if(user != null && SecurityUtils.verifyPassword(loginRequest.password, user.passwordHash!! )){
                return@withContext user
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }


    fun sendMailConfirmation(user : UserDto, otp : String) : Boolean{
        if(user.mail == null || user.password == null) return false
        return MailService.sendOtpEmail(user.mail, otp)
    }

    suspend fun checkUserAlreadyRegistered(u : UserDto) : Boolean{
        val userExists = pendingUserCollection.findOne(User::mail eq u.mail) != null
        return userExists
    }

    suspend fun checkUserMailExists(mail : String) : Boolean{
        val userExists = userCollection.findOne(User::mail eq mail) != null
        return userExists
    }

    suspend fun createPendingUser(newUser : UserDto, otp : String) : Boolean = withContext(Dispatchers.IO) {
        try{
            val user = PendingUser(
                otp = SecurityUtils.hashPassword(otp),
                username = newUser.username,
                mail = newUser.mail,
                passwordHash = newUser.password?.let { SecurityUtils.hashPassword(it) },
                roles = newUser.roles,
                createdAt = System.currentTimeMillis()
            )
            pendingUserCollection.save(user)
        }catch (e: Exception){
            e.printStackTrace()
            return@withContext false
        }


        return@withContext true
    }


    suspend fun createUserFromPendingUser(mail : String, oneTimePassword : String) : Boolean = withContext(Dispatchers.IO) {
        val pendingUser = pendingUserCollection.findOne(PendingUser::mail eq mail) ?: return@withContext false
        if(!(SecurityUtils.verifyPassword(oneTimePassword, pendingUser.otp))) return@withContext false
        //if(pendingUser.createdAt < System.currentTimeMillis()) return@withContext false

        val user = User(
            username = pendingUser.username,
            mail = pendingUser.mail,
            passwordHash = pendingUser.passwordHash,
            roles = pendingUser.roles,
            createdAt = pendingUser.createdAt
        )
        userCollection.insertOne(user)
        pendingUserCollection.deleteOne(PendingUser::id eq pendingUser.id)
        return@withContext true
    }










}

