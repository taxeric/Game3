package com.lanier.game.feature.user

import com.lanier.game.model.UserModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/**
 * Created by 幻弦让叶
 * Date 2024/9/22 00:15
 */
fun Application.installUserModule() {

    val dao: UserDao = UserDaoImpl()

    routing {
        get("/get-user") {
            val account = call.request.queryParameters["account"]
            val password = call.request.queryParameters["password"]
            if (account.isNullOrBlank()) {
                call.respond(respError<UserModel>(message = "Missing account parameter"))
                return@get
            }
            if (password.isNullOrBlank()) {
                call.respond(respError<UserModel>(message = "Missing password parameter"))
                return@get
            }

            val user = dao.getUser(account = account, password = password)
            if (user == null) {
                call.respond(respError<UserModel>(message = "User not found"))
                return@get
            }

            call.respond(respSuccess<UserModel>(data = user))
        }
    }
}

