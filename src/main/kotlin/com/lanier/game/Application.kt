package com.lanier.game

import com.lanier.game.feature.user.installUserModule
import com.lanier.game.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()

    DatabaseFactory.init()

    installFeatureModules()
}

/**
 * install the feature modules in here
 */
fun Application.installFeatureModules() {
    installUserModule()
}

