package com.makassar


import AuthService
import BagPartService
import CustomerService
import OrderService
import BagService
import UserService
import bagRoutes
import bagSubPartRoutes
import com.makassar.auth.JWTConfig
import com.makassar.plugins.configureCORS
import com.makassar.storage.DatabaseConfig
import com.makassar.plugins.configureRouting
import com.makassar.plugins.configureSecurity
import com.makassar.plugins.configureSerialization
import com.makassar.routes.authRoutes
import com.typesafe.config.ConfigFactory
import customersRoutes

import io.ktor.server.application.*
import io.ktor.server.config.*

import ordersRoutes
import usersRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    println()
    println(environment.config.toMap())
    println()

    val audience = environment.config.tryGetString("ktor.appConfig.environment.jwt.audience") ?: "audience"
    val issuer = environment.config.tryGetString("ktor.appConfig.environment.jwt.issuer") ?: "issuer"
    val secret = environment.config.tryGetString("ktor.appConfig.environment.jwt.secret") ?: "secret"
    val realm = environment.config.tryGetString("ktor.appConfig.environment.jwt.realm") ?: "myRealm"
    val accessTokenLifeTime = environment.config.tryGetString("ktor.appConfig.environment.jwt.accessToken.lifetime")?.toLong() ?: (1000 * 60*1)
    val refreshTokenLifeTime = environment.config.tryGetString("ktor.appConfig.environment.jwt.refreshToken.lifetime")?.toLong() ?: (1000* 60 * 60 * 24 * 1)

    val jwtConfig = JWTConfig(audience,issuer,secret, realm)

    configureSerialization()
    configureSecurity(jwtConfig)
    configureRouting()
    configureCORS()

    val mongoDatabase = DatabaseConfig.getDatabase(environment)

    val orderService = OrderService(mongoDatabase)
    val customerService = CustomerService(mongoDatabase)
    val bagService = BagService(mongoDatabase)
    val bagItemService = BagPartService(mongoDatabase)


    val userService = UserService(mongoDatabase)
    val authService = AuthService(mongoDatabase)



    authRoutes(jwtConfig,authService, accessTokenLifeTime, refreshTokenLifeTime)
    usersRoutes(userService)

    ordersRoutes(orderService)
    customersRoutes(customerService)
    bagRoutes(bagService)
    bagSubPartRoutes(bagItemService)

}
