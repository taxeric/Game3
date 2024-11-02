package com.lanier.game.feature.farm.market

import com.lanier.game.feature.farm.seed.SeedDao
import com.lanier.game.feature.farm.seed.SeedDaoImpl
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:24
 */
fun Application.installMarketModule() {

    val marketDao: MarketDao = MarketDaoImpl()
    val seedDao: SeedDao = SeedDaoImpl()

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

            post("/change-listed") {
                val jsonString = call.receiveText()
                val json = Json.parseToJsonElement(jsonString).jsonObject
                val marketIdStr = json["marketId"]?.jsonPrimitive?.content
                val listedStr = json["listed"]?.jsonPrimitive?.content

                if (marketIdStr.isNullOrBlank() || listedStr.isNullOrBlank()) {
                    call.respond(respError<Boolean>(message = "invalid params"))
                    return@post
                }

                val marketId = marketIdStr.toIntOrNull()
                val listed = listedStr.toBoolean()

                if (marketId == null || marketId < 0) {
                    call.respond(respError<Boolean>(message = "invalid id param"))
                    return@post
                }

                val result = marketDao.changeListedState(marketId, listed)
                if (result == null) {
                    call.respond(respError<Boolean>(message = "operation failed"))
                    return@post
                }

                call.respond(respSuccess(data = true))
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

        get("/market/add-all-seeds") {
            val seeds = seedDao.getAllSeeds()

            if (seeds.isNullOrEmpty()) {
                call.respond(respError<Boolean>())
                return@get
            }

            val marketItems = seeds.map { seed ->
                MarketAddReqDTOModel(
                    itemId = seed.seedId,
                    itemType = seed.itemType,
                    name = seed.name,
                    desc = seed.desc ?: "暂无说明",
                    price = seed.price,
                    isListed = true
                )
            }

            val result = marketDao.upsertProducts(marketItems)
            if (result.isNullOrEmpty()) {
                call.respond(respError<Boolean>(code = -101, message = "product already exists"))
                return@get
            }

            call.respond(respSuccess(data = true))
        }
    }
}
