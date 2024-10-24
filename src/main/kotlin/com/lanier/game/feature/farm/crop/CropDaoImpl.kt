package com.lanier.game.feature.farm.crop

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.CropAddReqDTOModel
import com.lanier.game.model.dto.CropRespDTOModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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

    override suspend fun getCropByName(name: String, limit: Int, offset: Int): List<CropRespDTOModel>? {
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
                    id = row[CropTable.id],
                    name = row[CropTable.name],
                    price = row[CropTable.price],
                    season = row[CropTable.season],
                    seedId = row[CropTable.seedId],
                )
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
            id = this[CropTable.id],
            name = this[CropTable.name],
            price = this[CropTable.price],
            season = this[CropTable.season],
            seedId = this[CropTable.seedId],
        )
    }
}