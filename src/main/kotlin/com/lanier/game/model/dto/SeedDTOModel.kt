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
    val cropExpPer: Int,
    val singleHarvestAmount: Int,
    val season: Int,
    val stageInfo: String,
    val plantLevel: Int,
): BaseItem()

@Serializable
data class SeedAddReqDTOModel(
    val id: Int? = null,
    val name: String,
    val price: Int,
    val stageInfo: String,
    /**
     * 最大可以收获的次数
     */
    val maxHarvestCount: Int,
    /**
     * 每次收获作物果实时的数量
     */
    val singleHarvestAmount: Int,
    /**
     * 每个种子成熟后果实的经验
     */
    val cropExpPer: Int,
    val cropId: Int = -1,
    val season: Int = 0,
    val plantLevel: Int = 0,
    val addToCrop: Boolean = true,
) {

    fun valid(): Boolean {
        val withCrop = if (addToCrop) {
            cropId > 0
        } else {
            true
        }
        return withCrop &&
                name.isNotBlank() &&
                price > 0 &&
                maxHarvestCount > 0 &&
                singleHarvestAmount > 0 &&
                cropExpPer > 0 &&
                season >= 0 &&
                plantLevel >= 0 &&
                stageInfo.isNotBlank()
    }
}

@Serializable
data class SeedStageInfoDTOModel(
    val stageName: List<String>,
    val stageSustainTime: List<Int>,
)
