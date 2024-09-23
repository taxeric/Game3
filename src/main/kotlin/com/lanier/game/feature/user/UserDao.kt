package com.lanier.game.feature.user

import com.lanier.game.model.dto.UserRespDTOModel

/**
 * Created by 幻弦让叶
 * Date 2024/9/21 22:32
 */
interface UserDao {

    suspend fun getUser(account: String, password: String): UserRespDTOModel?

    suspend fun insertUser(uname: String, pword: String): Int?
}