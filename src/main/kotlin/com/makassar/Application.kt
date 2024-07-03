package com.makassar


import com.makassar.plugins.configureRouting
import com.makassar.plugins.configureSecurity
import com.makassar.plugins.configureSerialization
import com.makassar.plugins.connectToMongoDB
import io.ktor.server.application.*
import ordersRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    configureSecurity()
    configureRouting()

    val mongoDatabase = connectToMongoDB()
    ordersRoutes(mongoDatabase)

}
