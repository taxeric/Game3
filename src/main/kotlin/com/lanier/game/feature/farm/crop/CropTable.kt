package com.lanier.game.feature.farm.crop

import org.jetbrains.exposed.sql.Table

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/2 22:43
 */
private const val TABLE_CROP = "crop"

object CropTable : Table(TABLE_CROP) {

    val id = integer("id").autoIncrement()
    val seedId = integer("seed_id")
    val name = varchar("name", 50)
    val price = integer("price")
    val season = integer("season")
}