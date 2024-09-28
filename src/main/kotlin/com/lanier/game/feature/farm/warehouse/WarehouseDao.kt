package com.lanier.game.feature.farm.warehouse

import com.lanier.game.model.BaseItem
import com.lanier.game.model.dto.WarehouseRespDTOModel

interface WarehouseDao {

    suspend fun increase(
        userId: Int,
        itemType: Int,
        itemId: Int,
        quantity: Int
    ): Boolean?

    suspend fun consume(
        userId: Int,
        itemType: Int,
        itemId: Int,
        quantity: Int
    ): Boolean?

    suspend fun getMerchandiseByType(type: Int): List<WarehouseRespDTOModel<out BaseItem>>?
}