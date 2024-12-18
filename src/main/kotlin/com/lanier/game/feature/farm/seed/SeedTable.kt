package com.lanier.game.feature.farm.seed

import org.jetbrains.exposed.sql.Table

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 20:26
 */
private const val TABLE_SEED = "seed"

object SeedTable : Table(TABLE_SEED) {
    val id = integer("id")
    val cropId = integer("crop_id")
    val name = varchar("name", 50)
    val maxHarvestCount = integer("max_harvest_count")
    val cropExpPer = integer("crop_exp_per")
    val singleHarvestAmount = integer("single_harvest_amount")
    val season = integer("season").default(0)
    val price = integer("price")
    val stageInfo = text("stage_info")
    val plantLevel = integer("plant_level").default(0)
    val seedExp = integer("seed_exp").default(1)
    val desc = varchar("desc", 200)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}