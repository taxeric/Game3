package com.lanier.game.feature.farm.warehouse

import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 21:01
 */
fun Application.installWarehouseModule() {

    val warehouseDao: WarehouseDao = WarehouseDaoImpl()

    routing {

        get("/get-merchandise") {
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

            val merchandises = warehouseDao.getMerchandiseByType(queryType)
            call.respond(
                respSuccess(data = merchandises)
            )
        }
    }
}
