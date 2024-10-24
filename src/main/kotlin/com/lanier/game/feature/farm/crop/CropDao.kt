package com.lanier.game.feature.farm.crop

import com.lanier.game.model.dto.CropAddReqDTOModel
import com.lanier.game.model.dto.CropRespDTOModel

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/2 22:45
 */
interface CropDao {

    suspend fun upsertCrop(crop: CropAddReqDTOModel): Boolean?

    suspend fun getCropById(id: Int): CropRespDTOModel?

    suspend fun getCropByName(name: String, limit: Int, offset: Int): List<CropRespDTOModel>?

    suspend fun getAllCrops(limit: Int, offset: Int): List<CropRespDTOModel>?
}