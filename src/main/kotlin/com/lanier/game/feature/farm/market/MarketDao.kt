package com.lanier.game.feature.farm.market

import com.lanier.game.model.dto.MarketAddReqDTOModel
import com.lanier.game.model.dto.MarketRespDTOModel

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 18:54
 */
interface MarketDao {

    suspend fun getProductById(id: Int) : MarketRespDTOModel?

    suspend fun getAllProductsByType(
        type: Int,
        offset: Int,
        limit: Int,
    ) : List<MarketRespDTOModel>?

    suspend fun getAllListedProductsByType(
        type: Int,
        offset: Int,
        limit: Int,
        isListed: Boolean
    ) : List<MarketRespDTOModel>?

    suspend fun upsertProduct(model: MarketAddReqDTOModel) : Boolean?

    suspend fun changeListedState(marketId: Int, listed: Boolean) : Boolean?
}