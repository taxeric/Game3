package com.lanier.game.model.dto

import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Transient

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 10:30
 */
@Serializable
data class UserRegisterReqDTOModel(

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,
)

@Serializable
data class UserRespDTOModel(
    val id: Int,
    val account: String,
    val username: String,
    val gender: Int,
    val balance: Int,
    val lands: List<Land>? = null,
    val token: String = "",
)

@Serializable
data class UserRegisterRespDTOModel(
    @Transient val id: Int = 0,
    val account: String,
) {

    val validAccount: Boolean
        get() = id > 10000 && account.isNotBlank()
}