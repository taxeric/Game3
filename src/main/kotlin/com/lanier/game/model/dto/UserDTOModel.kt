package com.lanier.game.model.dto

import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 10:30
 */
@Serializable
data class UserRegisterDTO(

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
    val lands: List<Land>? = null,
    val token: String = "",
)