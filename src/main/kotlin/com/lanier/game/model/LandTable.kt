package com.lanier.game.model

import org.jetbrains.exposed.sql.Table

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 22:27
 */
private const val TABLE_LAND = "land"

object LandTable : Table(TABLE_LAND) {

    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val cropId = integer("crop_id")
    val toNextStageRemainingTime = long("to_next_stage_remaining_time")
    val lastHarvestTime = long("last_harvest_time")
    val maxHarvestCount = integer("max_harvest_count")
    val harvestCount = integer("harvest_count")
    val status = integer("status")

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}