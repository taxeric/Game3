package com.lanier.game.feature.user

import com.lanier.game.model.dto.UserRegisterRespDTOModel
import com.lanier.game.model.dto.UserRespDTOModel

/**
 * Created by 幻弦让叶
 * Date 2024/9/21 22:32
 */
interface UserDao {

    suspend fun getUser(account: String, password: String): UserRespDTOModel?

    suspend fun getUserById(id: Int): UserRespDTOModel?

    suspend fun getBalanceById(id: Int): Int?

    suspend fun insertUser(uname: String, pword: String): UserRegisterRespDTOModel?

    suspend fun updateBalanceById(id: Int, balance: Int): Boolean?
}