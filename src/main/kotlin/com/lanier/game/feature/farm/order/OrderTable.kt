package com.lanier.game.feature.farm.order

import org.jetbrains.exposed.sql.Table

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:16
 */
private const val TABLE_ORDER = "orders"

object OrderTable : Table(TABLE_ORDER) {

    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val marketItemId = integer("market_item_id")
    val quantity = integer("quantity")
    val type = integer("type")
    val timestamp = long("timestamp")
}