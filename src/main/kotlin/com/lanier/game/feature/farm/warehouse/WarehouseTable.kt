package com.lanier.game.feature.farm.warehouse

import org.jetbrains.exposed.sql.Table

private const val TABLE_WAREHOUSE = "warehouse"

object WarehouseTable : Table(TABLE_WAREHOUSE) {

    const val TYPE_SEED = 1
    const val TYPE_CROP = 2
    const val TYPE_PROP = 2

    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val itemType = integer("item_type")
    val itemId = integer("item_id")
    val quantity = integer("quantity")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}