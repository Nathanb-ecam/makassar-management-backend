package com.makassar.routes

import AuthService
import com.makassar.auth.JWTConfig
import com.makassar.dto.requests.LoginRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.authRoutes(
    jwtConfig: JWTConfig,
    authService: AuthService,
    accessTokenLifeTime : Long,
    refreshTokenLifeTime : Long,
){


    routing {
        post("/api/login") {
            try{
                val user : LoginRequest = call.receive<LoginRequest>()
                val matchedUser = authService.loginWithMail(user) ?: return@post call.respond(HttpStatusCode.Unauthorized,"wtf")

                val accessToken = jwtConfig.generateToken(mapOf("type" to "access" ),accessTokenLifeTime)
                val refreshToken = jwtConfig.generateToken(mapOf("type" to "refresh" ),refreshTokenLifeTime)

                call.response.cookies.append(
                    Cookie(
                        name = "refreshToken",
                        value = refreshToken,
                        httpOnly = true,
                        secure = true,
                        path = "/",
                        maxAge = refreshTokenLifeTime.toInt()
                    )
                )

                return@post call.respond(hashMapOf("accessToken" to accessToken))
            }catch(e: Exception){
                call.respond(HttpStatusCode.BadRequest,e.toString())
            }






        }
/*        authenticate("refresh-jwt"){
            get("/api/refresh-token") {


                val claims = mapOf("type" to "access")
                val newAccessToken = jwtConfig.generateToken(claims, 180_000)
                    call.respond(mapOf("accessToken" to newAccessToken))
                }
            }*/
        get("/api/refresh-token") {

            val refreshToken = call.request.cookies["refreshToken"]
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Refresh token cookie missing")

            val valid = jwtConfig.verifyRefreshToken(refreshToken) ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid refresh token")

            val claims = mapOf("type" to "access")
            val newAccessToken = jwtConfig.generateToken(claims, accessTokenLifeTime)
            call.respond(mapOf("accessToken" to newAccessToken))

        }
    }


}
