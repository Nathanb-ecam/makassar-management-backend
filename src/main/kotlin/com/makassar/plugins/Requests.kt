package com.makassar.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureCORS() {
    install(CORS) {
        //method(io.ktor.http.HttpMethod.Options)
        //header(io.ktor.http.HttpHeaders.XForwardedProto)
        anyHost()
    }
}



