package com.makassar.utils

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlin.random.Random

object SecurityUtils {

    fun hashPassword(plainPassword: String): String {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray()) // 12 = cost factor
    }


    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified
    }

    fun generateOTP() : String{
        return Random.nextInt(100000,999999).toString()
    }

}