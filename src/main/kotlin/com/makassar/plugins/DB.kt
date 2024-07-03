package com.makassar.plugins

import io.ktor.server.application.*
import io.ktor.server.config.*
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase


object DB {
    private var database: MongoDatabase? = null

    fun connectToMongoDB(environment: ApplicationEnvironment): MongoDatabase {
        return database ?: synchronized(this) {
            database ?: createMongoDBConnection(environment).also { database = it }
        }
    }

    private fun createMongoDBConnection(environment: ApplicationEnvironment): MongoDatabase {
        val user = environment.config.tryGetString("db.mongo.user")
        val password = environment.config.tryGetString("db.mongo.password")
        val host = environment.config.tryGetString("db.mongo.host") ?: "127.0.0.1"
        val port = environment.config.tryGetString("db.mongo.port") ?: "27017"
        val maxPoolSize = environment.config.tryGetString("db.mongo.maxPoolSize")?.toInt() ?: 20
        val databaseName = environment.config.tryGetString("db.mongo.database.name") ?: "myDatabase"

        val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
        val uri = "mongodb://$credentials$host:$port/?maxPoolSize=$maxPoolSize&w=majority"

        val mongoClient = MongoClients.create(uri)
        val db = mongoClient.getDatabase(databaseName)

        environment.monitor.subscribe(ApplicationStopped) {
            mongoClient.close()
        }

        return db
    }
}


fun Application.connectToMongoDB(): MongoDatabase {
    return DB.connectToMongoDB(environment)
}




