package com.lanier.game.feature.user

import com.lanier.game.model.UserModel

/**
 * Created by 幻弦让叶
 * Date 2024/9/21 22:32
 */
interface UserDao {

    suspend fun getUser(account: String, password: String): UserModel?
}