package com.lanier.game.model

import kotlinx.serialization.Serializable

/**
 * Created by 幻弦让叶
 * Date 2024/9/21 23:16
 */
@Serializable
open class RespModel<T>(
    val code: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val data: T?
)

fun <T> respSuccess(
    data: T? = null,
): RespModel<T> {
    return RespModel(
        code = 0,
        message = "success",
        data = data,
    )
}

fun <T> respError(
    code: Int = -1,
    message: String = "system error",
): RespModel<T> {
    return RespModel(
        code = code,
        message = message,
        data = null,
    )
}
