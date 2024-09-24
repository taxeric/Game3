package com.lanier.game.feature.farm.land

import com.lanier.game.model.dto.Land
import com.lanier.game.model.dto.LandPlantDto

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 22:37
 */
interface LandDao {

    suspend fun registerLandForUser(userId: Int) : List<Int>?

    suspend fun getLandsInfoByUid(userId: Int): List<Land>?

    suspend fun getLandStatusByLandId(userId: Int, landId: Int): Int?

    suspend fun updateLandInfo(userId: Int, land: LandPlantDto): Int?
}