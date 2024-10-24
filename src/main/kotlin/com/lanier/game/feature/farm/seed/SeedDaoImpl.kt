package com.lanier.game.feature.farm.seed

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.SeedAddReqDTOModel
import com.lanier.game.model.dto.SeedRespDTOModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 20:40
 */
class SeedDaoImpl : SeedDao {
    override suspend fun getSeedById(id: Int): SeedRespDTOModel? {
        return DatabaseFactory.process {
            transaction {
                val row = SeedTable
                    .selectAll()
                    .where { SeedTable.id eq id }
                    .singleOrNull()
                if (row == null) return@transaction null
                row.toSeed()
            }
        }
    }

    override suspend fun upsertSeed(seed: SeedAddReqDTOModel): Boolean? {
        return DatabaseFactory.process {
            transaction {
                if (seed.id == null) {
                    val row = SeedTable
                        .insert { statement ->
                            statement[name] = seed.name
                            statement[maxHarvestCount] = seed.maxHarvestCount
                            statement[cropExpPer] = seed.cropExpPer
                            statement[singleHarvestAmount] = seed.singleHarvestAmount
                            statement[season] = seed.season
                            statement[price] = seed.price
                            statement[stageInfo] = seed.stageInfo
                            statement[plantLevel] = seed.plantLevel
                            statement[cropId] = seed.cropId
                        }

                    row[SeedTable.id] > 0
                } else {
                    val result = SeedTable.update({ SeedTable.id eq seed.id }) { statement ->
                        statement[cropId] = seed.cropId
                        statement[name] = seed.name
                        statement[maxHarvestCount] = seed.maxHarvestCount
                        statement[cropExpPer] = seed.cropExpPer
                        statement[singleHarvestAmount] = seed.singleHarvestAmount
                        statement[season] = seed.season
                        statement[price] = seed.price
                        statement[stageInfo] = seed.stageInfo
                        statement[plantLevel] = seed.plantLevel
                    }
                    result > 0
                }
            }
        }
    }

    private fun ResultRow.toSeed(): SeedRespDTOModel {
        return SeedRespDTOModel(
            cropId = this[SeedTable.cropId],
            maxHarvestCount = this[SeedTable.maxHarvestCount],
            singleHarvestAmount = this[SeedTable.singleHarvestAmount],
            cropExpPer = this[SeedTable.cropExpPer],
            season = this[SeedTable.season],
            stageInfo = this[SeedTable.stageInfo],
            plantLevel = this[SeedTable.plantLevel],
        ).apply {
            name = this@toSeed[SeedTable.name]
            price = this@toSeed[SeedTable.price]
            itemId = this@toSeed[SeedTable.id]
        }
    }
}