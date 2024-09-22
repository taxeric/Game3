package com.lanier.game.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

/**
 * Created by 幻弦让叶
 * Date 2024/9/22 23:08
 */
var jwtSecret = "secret"
var jwtIsuser = "http://0.0.0.0:8080/"
val myRealm = "Access to 'hello'"

fun Application.configureJWT() {

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience()
                    .withIssuer(jwtIsuser)
                    .build()
            )

            validate { credential ->
                val claim = credential.payload.getClaim("account").asString()
                if (claim.isNullOrBlank().not()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

}