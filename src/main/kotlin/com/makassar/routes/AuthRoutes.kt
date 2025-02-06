package com.makassar.routes

import AuthService
import com.makassar.auth.JWTConfig
import com.makassar.dto.UserDto
import com.makassar.dto.requests.ConfirmUserRequest
import com.makassar.dto.requests.LoginRequest
import com.makassar.utils.SecurityUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random


fun Application.authRoutes(
    jwtConfig: JWTConfig,
    authService: AuthService,
    accessTokenLifeTime : Long,
    refreshTokenLifeTime : Long,
){


    routing {
        route("/api"){
            post("/login") {
                try{
                    val user : LoginRequest = call.receive<LoginRequest>()
                    val matchedUser = authService.loginWithMail(user) ?: return@post call.respond(HttpStatusCode.Unauthorized,"User doesn't exist")

                    val accessToken = jwtConfig.generateToken(mapOf("type" to "access" ),accessTokenLifeTime)
                    val refreshToken = jwtConfig.generateToken(mapOf("type" to "refresh" ),refreshTokenLifeTime)

                    call.response.cookies.append(
                        Cookie(
                            name = "refreshToken",
                            value = refreshToken,
                            httpOnly = true,
                            secure = true,
                            path = "/",
                            domain = "http://localhost:8080",
                            maxAge = refreshTokenLifeTime.toInt()
                        )
                    )

                    return@post call.respond(hashMapOf("accessToken" to accessToken, "tenantId" to matchedUser.id))
                }catch(e: Exception){
                    call.respond(HttpStatusCode.BadRequest,e.toString())
                }
            }

            post("/register"){
                val user = call.receive<UserDto>()
                if(user.mail == null || user.password == null) return@post call.respond(HttpStatusCode.BadRequest,"Missing fields: username and/or password ")

                val alreadyRegisterd = authService.checkUserAlreadyRegistered(user)
                if(alreadyRegisterd) return@post call.respond(HttpStatusCode.BadRequest, "User already exists")

                val otp = SecurityUtils.generateOTP()
                println("OTP " +otp)
                val mailSent = authService.sendMailConfirmation(user, otp)
                if(!mailSent) return@post call.respond(HttpStatusCode.BadRequest,"Error trying to sent the confirmation mail.")
                val savedPendingUser = authService.createPendingUser(user, otp)
                if(!savedPendingUser) return@post call.respond(HttpStatusCode.BadRequest,"Pending user was not created")
                return@post call.respond(HttpStatusCode.OK, "Email confirmation sent")
            }

            get("/verify") {
                val mail = call.parameters["mail"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Mail was not provided")
                val otp = call.parameters["otp"] ?: return@get call.respond(HttpStatusCode.BadRequest, "OTP was not provided")

                val created = authService.createUserFromPendingUser(mail, otp)
                if (created) {
                    val htmlResponse = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Account Confirmation</title>
            </head>
            <body>
                <h1>Account Successfully Created!</h1>
                <p>Your account has been confirmed and created successfully.</p>
                <p><a href="/">Go back to the homepage</a></p>
            </body>
            </html>
        """.trimIndent()

                    call.respondText(htmlResponse, contentType = ContentType.Text.Html)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Something went wrong :( ")
                }

            }

            /*        authenticate("refresh-jwt"){
                        get("/api/refresh-token") {


                            val claims = mapOf("type" to "access")
                            val newAccessToken = jwtConfig.generateToken(claims, 180_000)
                                call.respond(mapOf("accessToken" to newAccessToken))
                            }
                        }*/
            get("/refresh-token") {
                val cookies = call.request.cookies
                println("Received cookies: $cookies")
                val refreshToken = call.request.cookies["refreshToken"]
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Refresh token cookie missing")

                val valid = jwtConfig.verifyRefreshToken(refreshToken) ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid refresh token")

                val claims = mapOf("type" to "access")
                val newAccessToken = jwtConfig.generateToken(claims, accessTokenLifeTime)
                call.respond(mapOf("accessToken" to newAccessToken))

            }
        }

    }


}
