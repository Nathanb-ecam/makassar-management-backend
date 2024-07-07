package com.makassar.routes

import AuthService
import com.makassar.auth.JWTConfig
import com.makassar.auth.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.authRoutes(
    jwtConfig: JWTConfig,
    authService: AuthService
){

    routing {
        post("/api/login") {
            val user = call.receive<LoginRequest>()
            val matchedUser = authService.loginWithMail(user) ?: return@post call.respond(HttpStatusCode.Unauthorized)


            if(matchedUser.roles != null && matchedUser.roles.contains("admin")) {
                val adminClaims = mapOf("userIdentifier" to user.email,"admin" to "true")
                val token = jwtConfig.generateToken(adminClaims,180_000)
                return@post call.respond(hashMapOf("admin token" to token))
            }else{
                val basicClaims = mapOf("userIdentifier" to user.email)
                val token = jwtConfig.generateToken(basicClaims,3600_000)
                return@post call.respond(hashMapOf("basic token" to token))
            }


        }
    }
}