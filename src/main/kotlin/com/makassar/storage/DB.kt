package com.makassar.storage

import com.mongodb.ConnectionString
import io.ktor.server.application.*
import io.ktor.server.config.*

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory


object DatabaseConfig {

    private var client: CoroutineClient? = null
    private var database: CoroutineDatabase? = null

    private val logger = LoggerFactory.getLogger("DB")

    @Synchronized
    fun getDatabase(environment: ApplicationEnvironment): CoroutineDatabase {
        return database ?: init(environment).also { database = it }
    }

    private fun init(environment: ApplicationEnvironment): CoroutineDatabase {
        val user = environment.config.tryGetString("ktor.database.mongo.user")
        val password = environment.config.tryGetString("ktor.database.mongo.password")
        val host = environment.config.tryGetString("ktor.database.mongo.host") ?: "127.0.0.1"
        val port = environment.config.tryGetString("ktor.database.mongo.port") ?: "27017"
        val maxPoolSize = environment.config.tryGetString("ktor.database.mongo.maxPoolSize")?.toInt() ?: 20
        val databaseName = environment.config.tryGetString("ktor.database.mongo.dbname") ?: "myDatabase"

        val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
        val uri = "mongodb://$credentials$host:$port/?maxPoolSize=$maxPoolSize&w=majority"

        val newClient = KMongo.createClient(ConnectionString(uri)).coroutine
        val newDatabase = newClient.getDatabase(databaseName)

        client = newClient
        database = newDatabase


        logger.info("MongoDB : Database $databaseName connected")

        environment.monitor.subscribe(ApplicationStopped) {
            newClient.close()
        }

        return newDatabase
    }
}






