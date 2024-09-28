package com.lanier.game.feature.farm.market

import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:24
 */
fun Application.installMarketModule() {

    val marketDao: MarketDao = MarketDaoImpl()

    routing {

        get("/get-products") {
            val type = call.request.queryParameters["type"]
            if (type.isNullOrBlank()) {
                call.respond(
                    respError<Boolean>(message = "invalid type [$type]")
                )
                return@get
            }

            val queryType = type.toIntOrNull() ?: -1
            if (queryType == -1) {
                call.respond(
                    respError<Boolean>(message = "invalid type [$type]")
                )
                return@get
            }

            val products = marketDao.getAllProductsByType(queryType)
            if (products == null) {
                call.respond(
                    respError<Boolean>(message = "no produces of type [$type] were found")
                )
                return@get
            }

            call.respond(
                respSuccess(data = products)
            )
        }
    }
}
