
import com.makassar.dto.CustomerDto
import com.makassar.entities.Customer
import com.makassar.services.CRUDService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.UUID


class CustomerService(private val database: CoroutineDatabase) : CRUDService<CustomerDto,Customer> {
    private val customerCollection = database.getCollection<Customer>()

    override suspend fun createOne(new: CustomerDto): String = withContext(Dispatchers.IO) {

        val customer = Customer(
            id = UUID.randomUUID().toString(),
            name = new.name,
            mail = new.mail,
            country = new.country,
            shippingAddress = new.shippingAddress,
            createdAt = System.currentTimeMillis(),
        )

        customerCollection.insertOne(customer)
        customer.id
    }

    override suspend fun getAll(): List<Customer> = withContext(Dispatchers.IO) {
        customerCollection.find().toList()
    }


    override suspend fun getOneById(id: String): Customer? = withContext(Dispatchers.IO) {
        val customer = customerCollection.findOneById(id)
        customer
    }

    override suspend fun updateOneById(id: String, updated: CustomerDto): Boolean = withContext(Dispatchers.IO) {
        val existingCustomer = customerCollection.findOneById(id)
        if (existingCustomer != null) {
            val updatedCustomer = existingCustomer.copy(
                name = updated.name ?: existingCustomer.name,
                mail = updated.mail ?: existingCustomer.mail,
                country = updated.country ?: existingCustomer.country,
                shippingAddress = updated.shippingAddress ?: existingCustomer.shippingAddress,
                phone = updated.phone ?: existingCustomer.phone,
                updatedAt = System.currentTimeMillis(),
            )
            val result = customerCollection.replaceOneById(id, updatedCustomer)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(id: String): Boolean = withContext(Dispatchers.IO) {
        val result = customerCollection.deleteOneById(id)
        result.wasAcknowledged()
    }



}

