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
import java.io.File

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

        get("/new") {
            val originFile = File("src/main/origin/seeds/farm_seeds.txt")
            val content = originFile.readText()

            val regex = Regex("(\\w+)=\"(.*?)\"")

            fun parseToSeedModel(attributes: Map<String, String>): SeedAddReqDTOModel? {
                val mid = attributes["id"] ?: "-1"
                val picPathStr = mid.replace("100728", "")
                val picFolder = File("src/main/resources/seed_pics/$picPathStr/")
                if (picFolder.exists().not()) {
                    return null
                }
                val childFilesSize = picFolder.listFiles()?.size ?: 3
                val grownTime = attributes["grownTime"]?.toIntOrNull() ?: 0
                val stageInfo = buildStageInfo(childFilesSize - 2, grownTime)
                return SeedAddReqDTOModel(
                    id = mid.toIntOrNull(),
                    name = attributes["name"] ?: "Unknown",
                    maxHarvestCount = attributes["seasonN"]?.toIntOrNull() ?: 0,
                    singleHarvestAmount = attributes["harvestN"]?.toIntOrNull() ?: 0,
                    cropExpPer = attributes["harvestExpdes"]?.toIntOrNull() ?: 0,
                    cropId = attributes["harvestId"]?.toIntOrNull() ?: -1,
                    plantLevel = attributes["buyLevel"]?.toIntOrNull() ?: 0,
                    season = 0,
                    price = 0,
                    stageInfo = stageInfo,
                )
            }

            val seedModels = content.split(Regex("<ManorSeedDes")).filter { it.contains("id=") }.mapNotNull { node ->
                val attributes = regex.findAll(node).associate { it.groupValues[1] to it.groupValues[2] }
                parseToSeedModel(attributes)
            }

            val seeds = seedModels.sortedBy { it.id }

            val insertRows = seedDao.upsertSeeds(seeds)
            if (insertRows == null) {
                call.respond(respError<Boolean>(code = -100, message = "system error"))
                return@get
            }

            call.respond(respSuccess(data = insertRows.size))
        }
    }
}

private fun buildStageInfo(size: Int, minutes: Int): String {
    val baseValue = minutes / size
    val remainder = minutes % size
    val result = MutableList(size) { baseValue }
    if (remainder > 0) {
        result[0] += remainder
    }
    val calcResult = result.joinToString(", ")
    return buildString {
        when (size) {
            3 -> {
                append("{\"stageName\":[")
                append("\"幼苗\", \"小叶子\", \"大叶子\", \"成熟\"")
                append("],")
                append("\"stageSustainTime\":[")
                append(calcResult)
                append("]}")
            }
            4 -> {
                append("{\"stageName\":[")
                append("\"幼苗\", \"小叶子\", \"中叶子\", \"大叶子\", \"成熟\"")
                append("],")
                append("\"stageSustainTime\":[")
                append(calcResult)
                append("]}")
            }
            else -> {
            }
        }
    }
}
