package com.lanier.game.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.lanier.game.model.respError
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.*

/**
 * Created by 幻弦让叶
 * Date 2024/9/22 23:08
 */
var jwtSecret = "secret" // 密钥
var jwtIssuer = "lanier://prj/game3" // 签发者
val myRealm = "Access to 'lanier.game3'"

const val AUTH_JWT = "auth-jwt"

fun Application.configureJWT() {

    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = myRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build()
            )

            validate { credential ->
                val issuer = credential.payload.issuer
                val currentTime = System.currentTimeMillis() / 1000L
                val issuedAt = credential.payload.issuedAt?.time?.div(1000) // iat（签发时间）的秒数
                val expiresAt = credential.payload.expiresAt?.time?.div(1000) // exp（过期时间）的秒数

                if (issuer != jwtIssuer) {
                    return@validate null
                }
                // 验证 Token 是否在有效时间范围内
                if (issuedAt == null || expiresAt == null || currentTime < issuedAt || currentTime > expiresAt) {
                    return@validate null
                }
                JWTPrincipal(credential.payload)
            }

            challenge { defaultScheme, realm ->
                call.respond(respError<Boolean>(code = 90000, message = "Unauthorized"))
            }
        }
    }
}