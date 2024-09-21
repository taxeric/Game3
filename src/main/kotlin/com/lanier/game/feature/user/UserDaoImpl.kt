package com.lanier.game.feature.user

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.UserModel
import com.lanier.game.model.UserTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.transactions.transaction

class UserDaoImpl : UserDao {
    override suspend fun getUser(account: String, password: String): UserModel? {
        return DatabaseFactory.query {
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
            resultRow ?: return@query null
            UserModel(
                id = resultRow[UserTable.id],
                account = resultRow[UserTable.account],
                password = resultRow[UserTable.password],
                username = resultRow[UserTable.username],
                gender = resultRow[UserTable.gender],
            )
        }
    }
}