package com.makassar


import AuthService
import BagPartService
import CustomerService
import OrderService
import BagPartMaterialService
import BagService
import ColorService
import MaterialTypeService
import UserService
import bagPartMaterialRoutes
import bagRoutes
import bagSubPartRoutes
import colorRoutes
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
import materialTypeRoutes

import ordersRoutes
import usersRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val audience = environment.config.tryGetString("jwt.audience") ?: "audience"
    val issuer = environment.config.tryGetString("jwt.issuer") ?: "issuer"
    val secret = environment.config.tryGetString("jwt.secret") ?: "secret"
    val realm = environment.config.tryGetString("jwt.realm") ?: "myRealm"
    val accessTokenLifeTime = environment.config.tryGetString("jwt.accessToken.lifetime")?.toLong() ?: (1000 * 60*1)
    val refreshTokenLifeTime = environment.config.tryGetString("jwt.refreshToken.lifetime")?.toLong() ?: (1000* 60 * 60 * 24 * 1)

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
    val bagPartMaterialService = BagPartMaterialService(mongoDatabase)
    val materialTypeService = MaterialTypeService(mongoDatabase)
    val colorService = ColorService(mongoDatabase)

    val userService = UserService(mongoDatabase)
    val authService = AuthService(mongoDatabase)



    authRoutes(jwtConfig,authService, accessTokenLifeTime, refreshTokenLifeTime)
    usersRoutes(userService)

    ordersRoutes(orderService)
    customersRoutes(customerService)
    bagRoutes(bagService)
    bagSubPartRoutes(bagItemService)
    bagPartMaterialRoutes(bagPartMaterialService)
    materialTypeRoutes(materialTypeService)
    colorRoutes(colorService)


}
