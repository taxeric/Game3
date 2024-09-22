package com.lanier.game.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

/**
 * Created by 幻弦让叶
 * Date 2024/9/21 22:38
 */
const val TABLE_NAME = "user"

object UserTable : Table(TABLE_NAME) {

    val id = integer("id").autoIncrement()
    val account = varchar("account", 20).uniqueIndex()
    val password = varchar("password", 20)
    val username = varchar("username", 50)
    val gender = integer("gender")

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}

@Serializable
data class UserModel(
    val id: Int,
    val account: String,
    val password: String,
    val username: String,
    val gender: Int,
    val token: String = "",
)
