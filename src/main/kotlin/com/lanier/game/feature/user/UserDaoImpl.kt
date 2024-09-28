package com.lanier.game.feature.user

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.UserRespDTOModel
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class UserDaoImpl : UserDao {
    override suspend fun getUser(account: String, password: String): UserRespDTOModel? {
        return DatabaseFactory.process {
            val resultRow = transaction {
                UserTable
                    .select(
                        columns = listOf(
                            UserTable.id,
                            UserTable.account,
                            UserTable.username,
                            UserTable.gender,
                            UserTable.balance,
                        )
                    )
                    .andWhere { UserTable.account eq account }
                    .andWhere { UserTable.password eq password }
                    .singleOrNull()
            }
            resultRow ?: return@process null
            UserRespDTOModel(
                id = resultRow[UserTable.id],
                account = resultRow[UserTable.account],
                username = resultRow[UserTable.username],
                gender = resultRow[UserTable.gender],
                balance = resultRow[UserTable.balance],
            )
        }
    }

    override suspend fun getUserById(id: Int): UserRespDTOModel? {
        return DatabaseFactory.process {
            val resultRow = transaction {
                UserTable
                    .select(
                        columns = listOf(
                            UserTable.id,
                            UserTable.account,
                            UserTable.username,
                            UserTable.gender,
                            UserTable.balance,
                        )
                    )
                    .where { UserTable.id eq id }
                    .singleOrNull()
            }
            resultRow ?: return@process null
            UserRespDTOModel(
                id = resultRow[UserTable.id],
                account = resultRow[UserTable.account],
                username = resultRow[UserTable.username],
                gender = resultRow[UserTable.gender],
                balance = resultRow[UserTable.balance],
            )
        }
    }

    override suspend fun getBalanceById(id: Int): Int? {
        return DatabaseFactory.process {
            transaction {
                val row = UserTable
                    .select(UserTable.balance)
                    .where { UserTable.id eq id }
                    .singleOrNull()
                if (row == null) return@transaction null
                row[UserTable.balance]
            }
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

    override suspend fun updateBalanceById(id: Int, balance: Int): Boolean? {
        return DatabaseFactory.process {
            transaction {
                UserTable.update({ UserTable.id eq id }) { statement ->
                    statement[UserTable.balance] = balance
                }
                true
            }
        }
    }

    private fun obtainRandomAccount(): String {
        return UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()
    }
}