package com.lanier.game.model.dto

import com.lanier.game.model.BaseItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 18:55
 */
@Serializable
data class MarketReqDTOModel(
    val typeId: Int,
    val itemId: Int,
    val name: String,
    val price: Int,
    val desc: String,
    val isListed: Boolean,
)

@Serializable
data class MarketRespDTOModel(
    val id: Int,
    val isListed: Boolean,
): BaseItem() {

    fun validItem(): Boolean {
        return isListed && itemType > 0 && itemId > 0
    }
}