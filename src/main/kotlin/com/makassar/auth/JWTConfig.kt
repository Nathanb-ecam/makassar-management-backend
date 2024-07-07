package com.makassar.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import java.util.*
import kotlin.collections.Map


class JWTConfig(
    val audience : String,
    val issuer : String,
    val secret : String,
    val realm : String
){

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(claims : Map<String,String>, durationMillis : Long): String{
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            //.withClaim("userIdentifier", matchedUser.email)
            .withExpiresAt(Date(System.currentTimeMillis() + durationMillis))

        claims.forEach { (key, value) ->
            token.withClaim(key, value)
        }

        return token.sign(algorithm)
    }

    fun verifier(): JWTVerifier{
        return JWT
            .require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }


    fun validateCredentials(credential: JWTCredential,requiredClaimNames: List<String>): JWTPrincipal? {

        requiredClaimNames.forEach { name ->
            val currentClaim = credential.payload.getClaim(name).asString()
            if (currentClaim.isEmpty()) return null

        }
        //val username = credential.payload.getClaim("userIdentifier").asString()
        return JWTPrincipal(credential.payload)

    }

}
