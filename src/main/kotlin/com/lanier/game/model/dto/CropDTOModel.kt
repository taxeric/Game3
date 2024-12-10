package com.lanier.game.model.dto

import com.lanier.game.model.BaseItem
import kotlinx.serialization.Serializable

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/2 22:46
 */
@Serializable
data class CropRespDTOModel(
    val cropId: Int,
    val seedId: Int,
    val season: Int,
): BaseItem(TYPE_CROP)

@Serializable
data class CropAddReqDTOModel(
    val id: Int? = null,
    val name: String,
    val price: Int,
    val seedId: Int = -1,
    val season: Int = 0,
)

