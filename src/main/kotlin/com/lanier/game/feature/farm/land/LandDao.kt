package com.lanier.game.feature.farm.land

import com.lanier.game.model.dto.Land

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 22:37
 */
interface LandDao {

    suspend fun registerLandForUser(userId: Int) : List<Int>?

    suspend fun getLandsInfoByUid(userId: Int): List<Land>?
}