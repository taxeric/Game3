package com.lanier.game.feature.user

import com.lanier.game.model.UserModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import io.ktor.server.application.Application
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Created by 幻弦让叶
 * Date 2024/9/22 00:15
 */
fun Application.installUserModule() {

    val dao: UserDao = UserDaoImpl()

    routing {
        post("/login") {

            val jsonString = call.receiveText()
            val json = Json.parseToJsonElement(jsonString).jsonObject
            val account = json["account"]?.jsonPrimitive?.content
            val password = json["password"]?.jsonPrimitive?.content

            if (account.isNullOrBlank()) {
                call.respond(respError<UserModel>(message = "Missing account parameter"))
                return@post
            }
            if (password.isNullOrBlank()) {
                call.respond(respError<UserModel>(message = "Missing password parameter"))
                return@post
            }

            val user = dao.getUser(account = account, password = password)
            if (user == null) {
                call.respond(respError<UserModel>(message = "User not found"))
                return@post
            }

            call.respond(respSuccess<UserModel>(data = user))
        }
    }
}

