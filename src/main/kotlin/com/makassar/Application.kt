package com.makassar


import AuthService
import CustomerService
import OrderService
import ItemMaterialService
import BagService
import UserService
import com.makassar.auth.JWTConfig
import com.makassar.plugins.configureCORS
import com.makassar.storage.DatabaseConfig
import com.makassar.plugins.configureRouting
import com.makassar.plugins.configureSecurity
import com.makassar.plugins.configureSerialization
import com.makassar.routes.authRoutes
import customersRoutes

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import ordersRoutes
import productMaterialsRoutes
import productRoutes
import usersRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val audience = environment.config.tryGetString("jwt.audience") ?: "audience"
    val issuer = environment.config.tryGetString("jwt.issuer") ?: "issuer"
    val secret = environment.config.tryGetString("jwt.secret") ?: "secret"
    val realm = environment.config.tryGetString("jwt.realm") ?: "myRealm"

    val jwtConfig = JWTConfig(audience,issuer,secret, realm)

    configureSerialization()
    configureSecurity(jwtConfig)
    configureRouting()
    configureCORS()

    val mongoDatabase = DatabaseConfig.getDatabase(environment)

    val orderService = OrderService(mongoDatabase)
    val customerService = CustomerService(mongoDatabase)
    val productService = BagService(mongoDatabase)
    val productMaterialService = ItemMaterialService(mongoDatabase)
    val userService = UserService(mongoDatabase)
    val authService = AuthService(mongoDatabase)



    authRoutes(jwtConfig,authService)
    usersRoutes(userService)

    ordersRoutes(orderService)
    customersRoutes(customerService)
    productRoutes(productService)
    productMaterialsRoutes(productMaterialService)


}
