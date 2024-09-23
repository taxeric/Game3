package com.lanier.game.feature.user

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.UserModel
import com.lanier.game.model.UserTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class UserDaoImpl : UserDao {
    override suspend fun getUser(account: String, password: String): UserModel? {
        return DatabaseFactory.process {
            val resultRow = transaction {
                UserTable
                    .select(
                        columns = listOf(
                            UserTable.id,
                            UserTable.account,
                            UserTable.password,
                            UserTable.username,
                            UserTable.gender,
                        )
                    )
                    .andWhere { UserTable.account eq account }
                    .andWhere { UserTable.password eq password }
                    .singleOrNull()
            }
            resultRow ?: return@process null
            UserModel(
                id = resultRow[UserTable.id],
                account = resultRow[UserTable.account],
                password = resultRow[UserTable.password],
                username = resultRow[UserTable.username],
                gender = resultRow[UserTable.gender],
            )
        }
    }

    override suspend fun insertUser(
        uname: String,
        pword: String,
    ): Int? {
        return DatabaseFactory.process {
            transaction {
                val insertStatement = UserTable.insert { statement ->
                    statement[username] = uname
                    statement[password] = pword
                    statement[account] = obtainRandomAccount()
                }
                val newId = insertStatement[UserTable.id]
                if (newId <= 0) return@transaction null
                val newAccount = "LaR$newId"
                UserTable.update({ UserTable.id eq newId }) {
                    it[account] = newAccount
                }

                newId
            }
        }
    }

    private fun obtainRandomAccount(): String {
        return UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()
    }
}