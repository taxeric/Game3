package com.lanier.game.model.dto

import kotlinx.serialization.Serializable

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/2 22:46
 */
@Serializable
data class CropRespDTOModel(
    val id: Int,
    val seedId: Int,
    val name: String,
    val price: Int,
    val season: Int,
)

@Serializable
data class CropAddReqDTOModel(
    val name: String,
    val price: Int,
    val seedId: Int = -1,
    val season: Int = 0,
)

