
import com.makassar.dto.CustomerDto
import com.makassar.entities.Customer
import com.makassar.services.GenericService
import com.mongodb.client.model.Filters.and
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.UUID


class CustomerService(private val database: CoroutineDatabase) : GenericService<CustomerDto,Customer> {
    private val customerCollection = database.getCollection<Customer>()

    override suspend fun createOne(new: CustomerDto): String = withContext(Dispatchers.IO) {

        val customer = Customer(
            id = UUID.randomUUID().toString(),
            userId = new.userId,
            name = new.name,
            mail = new.mail,
            phone = new.phone,
            tva = new.tva,
            type = new.type ?: "Professional",
            professionalAddress = new.professionalAddress,
            shippingAddress = new.shippingAddress,
            createdAt = System.currentTimeMillis(),
        )

        customerCollection.insertOne(customer)
        customer.id
    }

    override suspend fun getAll(userId: String): List<Customer> = withContext(Dispatchers.IO) {
        customerCollection.find(Customer::userId eq userId).toList()
    }


    override suspend fun getOneById(userId : String, id: String): Customer? = withContext(Dispatchers.IO) {
        val customer = customerCollection.findOne(and(Customer::id eq id, Customer::userId eq userId))
        customer
    }

    override suspend fun updateOneById(userId: String, id: String, updated: CustomerDto): Boolean = withContext(Dispatchers.IO) {
        val existingCustomer = customerCollection.findOne(and(Customer::id eq id, Customer::userId eq userId))
        if (existingCustomer != null) {
            val updatedCustomer = existingCustomer.copy(
                name = updated.name ?: existingCustomer.name,
                mail = updated.mail ?: existingCustomer.mail,
                tva = updated.tva ?: existingCustomer.tva,
                professionalAddress = updated.professionalAddress ?: existingCustomer.professionalAddress,
                shippingAddress = updated.shippingAddress ?: existingCustomer.shippingAddress,
                type = updated.type ?: existingCustomer.type,
                phone = updated.phone ?: existingCustomer.phone,
                updatedAt = System.currentTimeMillis(),
            )
            val result = customerCollection.replaceOne(and(Customer::id eq id, Customer::userId eq userId), updatedCustomer)
            result.wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun deleteOneById(userId: String, id: String): Boolean = withContext(Dispatchers.IO) {
        val result = customerCollection.deleteOne(and(Customer::id eq id, Customer::userId eq userId))
        result.wasAcknowledged()
    }



}

