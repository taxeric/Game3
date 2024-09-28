package com.lanier.game.model.dto

import kotlinx.serialization.Serializable

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:19
 */
@Serializable
data class OrderReqDTOModel(
    val userId: Int,
    val marketItemId: Int,
    val quantity: Int,
    val type: Int,
) {

    fun validOrder(): Boolean {
        return type > 0 && quantity > 0 && marketItemId > 0 && userId > 0
    }
}
