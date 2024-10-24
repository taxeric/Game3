package com.lanier.game.feature.farm.market

import com.lanier.game.model.dto.MarketAddReqDTOModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

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

        post("/upsert-product") {
            val addDto = try {
                val json = call.receiveText()
                Json.decodeFromString<MarketAddReqDTOModel>(json)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            addDto?: run {
                call.respond(respError<Boolean>(code = -100, message = "add product failed"))
                return@post
            }

            val result = marketDao.upsertProduct(addDto)
            if (result != true) {
                call.respond(respError<Boolean>(code = -101, message = "product already exists"))
                return@post
            }

            call.respond(respSuccess(data = true))
        }
    }
}
