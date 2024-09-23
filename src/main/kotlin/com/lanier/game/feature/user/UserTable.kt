package com.lanier.game.feature.user

import org.jetbrains.exposed.sql.Table

private const val TABLE_USER = "user"

object UserTable : Table(TABLE_USER) {

    val id = integer("id").autoIncrement()
    val account = varchar("account", 50).uniqueIndex()
    val username = varchar("username", 20)
    val password = varchar("password", 30)
    val gender = integer("gender")

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}