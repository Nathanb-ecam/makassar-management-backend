package com.makassar.plugins


import com.makassar.auth.JWTConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory


fun Application.configureSecurity(jwtConfig: JWTConfig) {
    install(Authentication) {
        val typeClaims = listOf("type")




        jwt("access-jwt") {

            realm = jwtConfig.realm
            verifier(jwtConfig.verifier(mapOf("type" to "access")))

            validate { credential ->
                jwtConfig.validateCredentials(credential,typeClaims)
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "accessToken is not valid or has expired")
            }
        }

/*        jwt("refresh-jwt") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier(mapOf("type" to "refresh")))

            validate { credential ->
                jwtConfig.validateCredentials(credential,typeClaims)
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "refreshToken is not valid or has expired")
            }
        }*/

    }

}


