package com.lanier.game.feature.farm.seed

import com.lanier.game.model.dto.SeedAddReqDTOModel
import com.lanier.game.model.dto.SeedRespDTOModel
import com.lanier.game.model.dto.SeedStageInfoDTOModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import com.lanier.game.plugins.AUTH_JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

/**
 * Desc:    
 * Author:  幻弦让叶
 * Date:    2024/10/2 21:57
 */
fun Application.installSeedModule() {

    val seedDao: SeedDao = SeedDaoImpl()

    routing {

        authenticate(AUTH_JWT) {

            post("/upsert-seed") {
                val addSeedDto = try {
                    val json = call.receiveText()
                    Json.decodeFromString<SeedAddReqDTOModel>(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                if (addSeedDto == null) {
                    call.respond(respError<Boolean>(message = "add seed failed"))
                    return@post
                }

                if (addSeedDto.valid().not()) {
                    call.respond(respError<Boolean>(code = -100, message = "add seed failed"))
                    return@post
                }

                val stage = try {
                    Json.decodeFromString<SeedStageInfoDTOModel>(addSeedDto.stageInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                if (stage == null || stage.stageName.size != stage.stageSustainTime.size) {
                    call.respond(respError<Boolean>(code = -101, message = "invalid stage information"))
                    return@post
                }

                val result = seedDao.upsertSeed(addSeedDto)
                if (result != true) {
                    call.respond(respError<Boolean>(code = -102, message = "add seed failed"))
                    return@post
                }

                call.respond(respSuccess(data = true))
            }

            get("/get-seed") {
                val seedId = call.request.queryParameters["id"]?.toIntOrNull()
                if (seedId == null) {
                    call.respond(respError<SeedRespDTOModel>(message = "invalid seed id"))
                    return@get
                }

                val seed = seedDao.getSeedById(seedId)
                if (seed == null) {
                    call.respond(respError<SeedRespDTOModel>(code = -100, message = "invalid seed id"))
                    return@get
                }

                call.respond(respSuccess(data = seed))
            }
        }
    }
}
