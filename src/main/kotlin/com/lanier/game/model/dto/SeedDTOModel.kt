package com.lanier.game.model.dto

import com.lanier.game.model.BaseItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 20:27
 */
@Serializable
data class SeedRespDTOModel(
    val cropId: Int,
    val maxHarvestCount: Int,
    val singleHarvestExp: Int,
    val singleHarvestAmount: Int,
    val season: Int,
    val stageInfo: String,
    val plantLevel: Int,
): BaseItem()
