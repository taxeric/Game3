package com.lanier.game.feature.farm.crop

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.CropAddReqDTOModel
import com.lanier.game.model.dto.CropRespDTOModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/2 22:47
 */
class CropDaoImpl : CropDao {
    override suspend fun upsertCrop(crop: CropAddReqDTOModel): Boolean? {
        return DatabaseFactory.process {
            transaction {
                if (crop.id == null) {
                    val result = CropTable.insert { statement ->
                        statement[name] = crop.name
                        statement[price] = crop.price
                        statement[season] = crop.season
                        statement[seedId] = crop.seedId
                    }

                    return@transaction result[CropTable.id] > 0
                }
                val result = CropTable.update({ CropTable.id eq crop.id }) { statement ->
                    statement[CropTable.seedId] = crop.seedId
                    statement[CropTable.name] = crop.name
                    statement[CropTable.price] = crop.price
                    statement[CropTable.season] = crop.season
                }
                return@transaction result <= 0
            }
        }
    }

    override suspend fun upsertCrops(crops: List<CropAddReqDTOModel>): List<Int>? {
        return DatabaseFactory.process {
            transaction {
                val resultRows = CropTable.batchInsert(
                    data = crops,
                    ignore = true,
                ) { crop ->
                    crop.id?.let {
                        this[CropTable.id] = crop.id
                        this[CropTable.seedId] = crop.seedId
                        this[CropTable.name] = crop.name
                        this[CropTable.price] = crop.price
                        this[CropTable.season] = crop.season
                    }
                }

                if (resultRows.isEmpty()) {
                    return@transaction null
                }

                val insertIds = resultRows.map {
                    it[CropTable.id]
                }

                return@transaction insertIds
            }
        }
    }

    override suspend fun getCropById(id: Int): CropRespDTOModel? {
        return DatabaseFactory.process {
            transaction {
                val row = CropTable
                    .selectAll()
                    .where { CropTable.id eq id }
                    .singleOrNull()
                if (row == null) return@transaction null
                row.toCrop()
            }
        }
    }

    override suspend fun getCropsByName(name: String, limit: Int, offset: Int): List<CropRespDTOModel>? {
        return DatabaseFactory.process {
            val queryRows = transaction {
                val rows = CropTable
                    .selectAll()
                    .where { CropTable.name like "%$name%" }
                    .limit(limit, offset.toLong())
                    .toList()

                rows
            }
            val crops = queryRows.map { row ->
                CropRespDTOModel(
                    cropId = row[CropTable.id],
                    season = row[CropTable.season],
                    seedId = row[CropTable.seedId],
                ).apply {
                    this.name = row[CropTable.name]
                    this.price = row[CropTable.price]
                }
            }
            crops
        }
    }

    override suspend fun getAllCrops(limit: Int, offset: Int): List<CropRespDTOModel>? {
        return DatabaseFactory.process {
            val queryRows = transaction {
                val rows = CropTable
                    .selectAll()
                    .limit(limit, offset.toLong())
                    .toList()

                rows
            }
            val crops = queryRows.map { row -> row.toCrop() }
            crops
        }
    }

    private fun ResultRow.toCrop(): CropRespDTOModel {
        return CropRespDTOModel(
            cropId = this[CropTable.id],
            season = this[CropTable.season],
            seedId = this[CropTable.seedId],
        ).apply {
            this.name = this@toCrop[CropTable.name]
            this.price = this@toCrop[CropTable.price]
        }
    }
}