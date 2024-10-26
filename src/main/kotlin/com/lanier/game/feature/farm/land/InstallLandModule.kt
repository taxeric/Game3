package com.lanier.game.feature.farm.land

import com.lanier.game.feature.farm.warehouse.WarehouseDao
import com.lanier.game.feature.farm.warehouse.WarehouseDaoImpl
import com.lanier.game.feature.farm.warehouse.WarehouseTable
import com.lanier.game.feature.user.UserDao
import com.lanier.game.feature.user.UserDaoImpl
import com.lanier.game.model.dto.Land
import com.lanier.game.model.dto.LandPlantDto
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import com.lanier.game.plugins.AUTH_JWT
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 22:37
 */
fun Application.installLandModule() {

    val userDao : UserDao = UserDaoImpl()
    val landDao : LandDao = LandDaoImpl()
    val warehouseDao : WarehouseDao = WarehouseDaoImpl()

    routing {

        authenticate(AUTH_JWT) {

            post("/land-plant") {
                val landPlantDto = try {
                    val receiveText = call.receiveText()
                    Json.decodeFromString<LandPlantDto>(receiveText)
                } catch (thr: Throwable) {
                    thr.printStackTrace()
                    null
                }

                if (landPlantDto == null) {
                    call.respond(respError<Boolean>())
                    return@post
                }

                val landStatus = landDao.getLandStatusByLandId(landPlantDto.userId, landPlantDto.landId)
                if (landStatus == null) {
                    call.respond(
                        respError<Boolean>(
                            code = -100,
                            message = "plant failed: unknown status"
                        )
                    )
                    return@post
                }

                if (landStatus == Land.UNLOCK || landStatus == Land.PLANTING) {
                    call.respond(
                        respError<Boolean>(
                            code = -101,
                            message = "plant failed: the land wasn't idle, $landStatus"
                        )
                    )
                    return@post
                }

                val consumeResult = warehouseDao.consume(
                    userId = landPlantDto.userId,
                    itemType = WarehouseTable.TYPE_SEED,
                    itemId = landPlantDto.seedId,
                    quantity = 1
                )

                if (consumeResult != true) {
                    call.respond(
                        respError<Boolean>(
                            code = -102,
                            message = "plant failed: consume warehouse item was failed"
                        )
                    )
                    return@post
                }

                val plantResult = landDao.plant(landPlantDto)
                if (plantResult != true) {
                    call.respond(
                        respError<Boolean>(
                            code = -103,
                            message = "plant failed"
                        )
                    )
                    return@post
                }

                call.respond(respSuccess(data = true))
            }
        }
    }
}
