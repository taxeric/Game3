package com.lanier.game.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/28 00:28
 */
fun Application.configStatic() {
    routing {
        staticFiles(
            remotePath = "/resources",
            dir = File("src/main/resources/seed_pics")
        )
    }
}
