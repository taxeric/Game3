package com.lanier.game.feature.farm.order

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.OrderReqDTOModel
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:22
 */
class OrderDaoImpl : OrderDao {
    override suspend fun create(model: OrderReqDTOModel): Boolean? {
        return DatabaseFactory.process {
            transaction {
                val resultRow = OrderTable.insert { statement ->
                    statement[userId] = model.userId
                    statement[marketItemId] = model.marketItemId
                    statement[quantity] = model.quantity
                    statement[type] = model.type
                }

                true
            }
        }
    }
}