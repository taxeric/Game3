package com.lanier.game

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Created by 幻弦让叶
 * Date 2024/9/21 21:50
 */
object DatabaseFactory {

    const val DRIVER = "com.mysql.cj.jdbc.Driver"
    const val DEFAULT_DATABASE_NAME = "dbname"
    const val USERNAME = "username"
    const val PASSWORD = "password"

    fun init() {
        val driverClassName = DRIVER
        val jdbcUrl = "jdbc:mysql://localhost:3306/$DEFAULT_DATABASE_NAME?serverTimezone=GMT%2B8&characterEncoding=utf8&useSSL=true"
        val username = USERNAME
        val password = PASSWORD
        Database.connect(jdbcUrl, driverClassName, username, password)
    }

    /**
     * run blocking data
     */
    suspend fun <T> process(block: suspend () -> T): T? {
        return newSuspendedTransaction(Dispatchers.IO) {
            block()
        }
    }
}