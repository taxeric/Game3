package com.lanier.game.feature.farm.seed

import com.lanier.game.model.dto.SeedAddReqDTOModel
import com.lanier.game.model.dto.SeedRespDTOModel

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 20:39
 */
interface SeedDao {

    suspend fun getSeedById(id: Int): SeedRespDTOModel?

    suspend fun upsertSeed(seed: SeedAddReqDTOModel): Boolean?
}