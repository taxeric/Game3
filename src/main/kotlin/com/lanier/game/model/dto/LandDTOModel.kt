package com.lanier.game.model.dto

import kotlinx.serialization.Serializable

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 23:38
 */
@Serializable
data class Land(
    val landId: Int = 0,
    val userId: Int,
    val cropId: Int = 0,
    val bpkId: Int = 0,

    val status: Int = UNLOCK,

    /**
     * 到下一阶段的剩余时间
     */
    val nextStageRemainTime: Long = 0L,

    /**
     * 上次收获时间
     */
    val lastHarvestTime: Long = 0L,

    /**
     * 最大收获次数
     */
    val maxHarvestCount: Int = 1,

    /**
     * 已收获次数
     */
    val harvestCount: Int = 0,
) {
    companion object {
        //未解锁
        const val UNLOCK = 1

        //种植中
        const val PLANTING = 2

        //空闲
        const val IDLE = 3
    }
}

@Serializable
data class LandPlantDto(
    val userId: Int,
    val landId: Int,
    val seedId: Int,
)