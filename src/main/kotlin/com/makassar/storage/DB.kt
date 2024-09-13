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

        val user = environment.config.propertyOrNull("ktor.appConfig.environment.database.mongo.user")?.getString() ?: "user"
        val password = environment.config.propertyOrNull("ktor.appConfig.environment.database.mongo.password")?.getString() ?: "password"

        val host = environment.config.propertyOrNull("ktor.appConfig.environment.database.mongo.host")?.getString() ?: "host"
        val port = environment.config.propertyOrNull("ktor.appConfig.environment.database.mongo.port")?.getString() ?: "3080"
        val maxPoolSize = environment.config.propertyOrNull("ktor.appConfig.environment.database.mongo.maxPoolSize")?.getString() ?: "24"
        val databaseName = environment.config.propertyOrNull("ktor.appConfig.environment.database.mongo.dbname")?.getString() ?: "databaseName"

        println(host)
        println(databaseName)

        val credentials = user.let { userVal -> password.let { passwordVal -> "$userVal:$passwordVal@" } }
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






