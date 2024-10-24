package com.lanier.game.feature.farm.crop

import com.lanier.game.model.dto.CropRespDTOModel
import com.lanier.game.model.respError
import com.lanier.game.model.respSuccess
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/2 23:04
 */
fun Application.installCropModule() {

    val cropDao: CropDao = CropDaoImpl()

    routing {

        get("/get-crop-by-id") {
            val cropId = call.request.queryParameters["id"]?.toIntOrNull()
            if (cropId == null) {
                call.respond(respError<CropRespDTOModel>(message = "invalid crop id"))
                return@get
            }

            val crop = cropDao.getCropById(cropId)
            if (crop == null) {
                call.respond(respError<CropRespDTOModel>(code = -101, message = "unknown crop"))
                return@get
            }

            call.respond(respSuccess(crop))
        }

        get("/get-crops-by-name") {
            val cropName = call.request.queryParameters["name"]
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?: 20
            val offset = call.request.queryParameters["offset"]?.toIntOrNull()

            if (offset == null || limit <= 0 || cropName.isNullOrBlank()) {
                call.respond(respError<List<CropRespDTOModel>>(message = "invalid crop params"))
                return@get
            }

            val crops = cropDao.getCropByName(name = cropName, limit = limit, offset = offset)
            call.respond(respSuccess(data = crops))
        }

        get("/get-crops") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?: 20
            val offset = call.request.queryParameters["offset"]?.toIntOrNull()
            if (offset == null || limit <= 0) {
                call.respond(respError<List<CropRespDTOModel>>(message = "invalid crop params"))
                return@get
            }


            val crops = cropDao.getAllCrops(limit, offset)
            call.respond(respSuccess(data = crops))
        }


    }
}
