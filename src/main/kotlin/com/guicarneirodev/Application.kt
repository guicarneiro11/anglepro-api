package com.guicarneirodev

import com.guicarneirodev.plugins.configureHTTP
import com.guicarneirodev.plugins.configureRouting
import com.guicarneirodev.plugins.configureSecurity
import com.guicarneirodev.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureRouting()
    configureHTTP()
}
