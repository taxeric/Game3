package com.lanier.game.feature.farm.market

import org.jetbrains.exposed.sql.Table

/**
 * Desc: 商城
 * Author: 幻弦让叶
 * Date 2024/9/28 18:49
 */

private const val TABLE_MARKET = "market"

object MarketTable : Table(TABLE_MARKET) {

    val id = integer("id").autoIncrement()
    val itemType = integer("item_type")
    val refItemId = integer("ref_item_id")
    val name = varchar("name", 50)
    val price = integer("price")
    val desc = varchar("desc", 100).default("")
    val isListed = bool("is_listed").default(true)
}