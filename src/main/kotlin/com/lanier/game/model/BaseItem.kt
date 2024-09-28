package com.lanier.game.model

import kotlinx.serialization.Serializable

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 20:13
 */
@Serializable
open class BaseItem {

    companion object {

        const val TYPE_SEED = 1
        const val TYPE_CROP = 2
        const val TYPE_PROP = 3

        fun validType(type: Int?): Boolean {
            return type != null && type in TYPE_SEED..TYPE_PROP
        }
    }

    var itemId: Int = -1
    var itemType: Int = -1
    var name: String = ""
    var price: Int = 0
    var desc: String? = ""
}
