package com.lanier.game.feature.farm.order

import com.lanier.game.feature.farm.market.MarketDao
import com.lanier.game.feature.farm.market.MarketDaoImpl
import com.lanier.game.feature.farm.warehouse.WarehouseDao
import com.lanier.game.feature.farm.warehouse.WarehouseDaoImpl
import com.lanier.game.feature.user.UserDao
import com.lanier.game.feature.user.UserDaoImpl
import com.lanier.game.model.dto.OrderReqDTOModel
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
 * Date 2024/9/28 19:36
 */
fun Application.installOrderModule() {

    val userDao: UserDao = UserDaoImpl()
    val orderDao: OrderDao = OrderDaoImpl()
    val marketDao: MarketDao = MarketDaoImpl()
    val warehouseDao: WarehouseDao = WarehouseDaoImpl()

    routing {
        post("/order-create") {
            val orderDto = try {
                val receiveText = call.receiveText()
                Json.decodeFromString<OrderReqDTOModel>(receiveText)
            } catch (thr: Throwable) {
                thr.printStackTrace()
                null
            }

            if (orderDto == null || orderDto.validOrder().not()) {
                call.respond(
                    respError<Boolean>(message = "order create failed: invalid order req")
                )
                return@post
            }

            val balance = userDao.getBalanceById(orderDto.userId)
            if (balance == null) {
                call.respond(
                    respError<Boolean>(message = "order create failed: get balance failed")
                )
                return@post
            }

            val marketItem = marketDao.getProductById(orderDto.marketItemId)
            if (marketItem == null || marketItem.validItem().not()) {
                call.respond(
                    respError<Boolean>(message = "order create failed: valid market item")
                )
                return@post
            }

            val balanceAfterPayment = balance - marketItem.price * orderDto.quantity
            if (balanceAfterPayment < 0) {
                call.respond(
                    respError<Boolean>(message = "order create failed: balance is insufficient")
                )
                return@post
            }

            val updateBalanceResult = userDao.updateBalanceById(orderDto.userId, balanceAfterPayment)
            if (updateBalanceResult != true) {
                call.respond(
                    respError<Boolean>(message = "order create failed: payment failed")
                )
                return@post
            }

            val addToWarehouseResult = warehouseDao.increase(
                userId = orderDto.userId,
                itemType = marketItem.itemType,
                itemId = marketItem.itemId,
                quantity = orderDto.quantity
            )
            if (addToWarehouseResult != true) {
                //todo 还原余额
                call.respond(
                    respError<Boolean>(message = "order create failed: add to warehouse failed")
                )
                return@post
            }

            val createResult = orderDao.create(orderDto)
            if (createResult != true) {
                //todo 还原余额和仓库
                call.respond(
                    respError<Boolean>(
                        code = -100,
                        message = "order create failed"
                    )
                )
                return@post
            }

            call.respond(respSuccess(data = createResult))
        }
    }
}
