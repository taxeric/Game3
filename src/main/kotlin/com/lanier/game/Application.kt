package com.lanier.game

import com.lanier.game.feature.farm.crop.installCropModule
import com.lanier.game.feature.farm.land.installLandModule
import com.lanier.game.feature.farm.market.installMarketModule
import com.lanier.game.feature.farm.order.installOrderModule
import com.lanier.game.feature.farm.seed.installSeedModule
import com.lanier.game.feature.farm.warehouse.installWarehouseModule
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
    configureJWT()
    configureRouting()
    configStatic()

    DatabaseFactory.init()

    installFeatureModules()
}

/**
 * install the feature modules in here
 */
fun Application.installFeatureModules() {
    installUserModule()
    installMarketModule()
    installSeedModule()
    installCropModule()
    installLandModule()
    installOrderModule()
    installWarehouseModule()
}

