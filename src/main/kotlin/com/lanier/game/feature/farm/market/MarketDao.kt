package com.lanier.game.feature.farm.market

import com.lanier.game.model.dto.MarketReqDTOModel
import com.lanier.game.model.dto.MarketRespDTOModel

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 18:54
 */
interface MarketDao {

    suspend fun getProductById(id: Int) : MarketRespDTOModel?

    suspend fun getAllProductsByType(type: Int) : List<MarketRespDTOModel>?

    suspend fun addProduct(model: MarketReqDTOModel) : Boolean?
}