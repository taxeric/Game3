package com.lanier.game.feature.farm.order

import com.lanier.game.model.dto.OrderReqDTOModel

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:20
 */
interface OrderDao {

    suspend fun create(model: OrderReqDTOModel) : Boolean?
}