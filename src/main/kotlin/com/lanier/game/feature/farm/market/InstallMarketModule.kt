package com.lanier.game.feature.farm.market

import com.lanier.game.model.dto.MarketAddReqDTOModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import com.lanier.game.plugins.AUTH_JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
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

        authenticate(AUTH_JWT) {

            get("/get-listed-products") {
                val type = call.request.queryParameters["type"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                if (type.isNullOrBlank() || limit <= 0 || offset == null) {
                    call.respond(
                        respError<Boolean>(message = "invalid type params")
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

                val products = marketDao.getAllListedProductsByType(queryType, offset, limit, true)
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

            get("/get-products") {
                val type = call.request.queryParameters["type"]
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val offset = call.request.queryParameters["offset"]?.toIntOrNull()
                if (type.isNullOrBlank() || limit <= 0 || offset == null) {
                    call.respond(
                        respError<Boolean>(message = "invalid type params")
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

                val products = marketDao.getAllProductsByType(queryType, offset, limit)
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

                addDto ?: run {
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
}
