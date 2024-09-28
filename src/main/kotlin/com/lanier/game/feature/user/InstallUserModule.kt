package com.lanier.game.feature.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.lanier.game.feature.farm.land.LandDao
import com.lanier.game.feature.farm.land.LandDaoImpl
import com.lanier.game.model.dto.UserRegisterDTO
import com.lanier.game.model.dto.UserRespDTOModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import com.lanier.game.plugins.jwtIsuser
import com.lanier.game.plugins.jwtSecret
import io.ktor.server.application.Application
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Date

/**
 * Created by 幻弦让叶
 * Date 2024/9/22 00:15
 */
fun Application.installUserModule() {

    val dao: UserDao = UserDaoImpl()
    val landDao: LandDao = LandDaoImpl()

    routing {
        post("/login") {

            val jsonString = call.receiveText()
            val json = Json.parseToJsonElement(jsonString).jsonObject
            val account = json["account"]?.jsonPrimitive?.content
            val password = json["password"]?.jsonPrimitive?.content

            if (account.isNullOrBlank()) {
                call.respond(respError<UserRespDTOModel>(message = "Missing account parameter"))
                return@post
            }
            if (password.isNullOrBlank()) {
                call.respond(respError<UserRespDTOModel>(message = "Missing password parameter"))
                return@post
            }

            val user = dao.getUser(account = account, password = password)
            if (user == null) {
                call.respond(respError<UserRespDTOModel>(message = "User not found"))
                return@post
            }

            val landInfos = landDao.getLandsInfoByUid(user.id)

            val token = JWT.create()
                .withIssuer(jwtIsuser)
                .withClaim("account", user.account)
                .withExpiresAt(Date(System.currentTimeMillis() + 1800000))
                .sign(Algorithm.HMAC256(jwtSecret))

            val respUser = user.copy(
                lands = landInfos,
                token = token,
            )

            call.respond(respSuccess(data = respUser))
        }

        post("/register") {
            val registerDto = try {
                val receiveText = call.receiveText()
                Json.decodeFromString<UserRegisterDTO>(receiveText)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            registerDto?: run {
                call.respond(respError<Boolean>(message = "register failed"))
                return@post
            }

            val username = registerDto.username
            val password = registerDto.password

            val id = dao.insertUser(username, password)

            if (id == null || id < 0) {
                call.respond(respError<Boolean>(code = -100, message = "register failed"))
                return@post
            }

            val ids = landDao.registerLandForUser(id)
            if (ids.isNullOrEmpty()) {
                call.respond(respError<Boolean>(code = -101, message = "register failed"))
                return@post
            }

            call.respond(respSuccess(data = true))
        }
    }
}

