package com.lanier.game.feature.user.dto

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