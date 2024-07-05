package com.makassar.plugins


import com.makassar.auth.JWTConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*


fun Application.configureSecurity(jwtConfig: JWTConfig) {


    install(Authentication) {
        val basicClaims = listOf("userIdentifier")
        val adminClaims = listOf("admin")

        jwt("basic-jwt") {

            realm = jwtConfig.realm
            verifier(jwtConfig.verifier())

            validate { credential ->
                jwtConfig.validateCredentials(credential,basicClaims)
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "BASIC Token is not valid or has expired")
            }
        }




        jwt("admin-jwt") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier())

            validate { credential ->
                jwtConfig.validateCredentials(credential,adminClaims)
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "ADMIN Token is not valid or has expired")
            }
        }

    }

}
