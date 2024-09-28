package com.lanier.game.model.dto

import com.lanier.game.model.BaseItem
import kotlinx.serialization.Serializable

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 20:10
 */
@Serializable
data class WarehouseRespDTOModel<T: BaseItem>(
    val id: Int,
    val item: T,
    val quantity: Int,
)