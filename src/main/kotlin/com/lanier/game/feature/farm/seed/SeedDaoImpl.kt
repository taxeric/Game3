package com.lanier.game.feature.farm.seed

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.SeedAddReqDTOModel
import com.lanier.game.model.dto.SeedRespDTOModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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
                            statement[seedExp] = seed.seedExp
                            statement[desc] = seed.desc
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
                        statement[seedExp] = seed.seedExp
                        statement[desc] = seed.desc
                    }
                    result > 0
                }
            }
        }
    }

    override suspend fun upsertSeeds(seeds: List<SeedAddReqDTOModel>): List<Int>? {
        return DatabaseFactory.process {
            transaction {
                val resultRows = SeedTable.batchInsert(
                    data = seeds,
                    ignore = true,
                ) { seed ->
                    seed.id?.let {
                        this[SeedTable.id] = seed.id
                        this[SeedTable.cropId] = seed.cropId
                        this[SeedTable.name] = seed.name
                        this[SeedTable.maxHarvestCount] = seed.maxHarvestCount
                        this[SeedTable.cropExpPer] = seed.cropExpPer
                        this[SeedTable.singleHarvestAmount] = seed.singleHarvestAmount
                        this[SeedTable.season] = seed.season
                        this[SeedTable.price] = seed.price
                        this[SeedTable.stageInfo] = seed.stageInfo
                        this[SeedTable.plantLevel] = seed.plantLevel
                        this[SeedTable.seedExp] = seed.seedExp
                        this[SeedTable.desc] = seed.desc
                    }
                }

                if (resultRows.isEmpty()) {
                    return@transaction null
                }

                val insertIds = resultRows.map {
                    it[SeedTable.id]
                }

                return@transaction insertIds
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
            seedExp = this[SeedTable.seedExp]
        ).apply {
            name = this@toSeed[SeedTable.name]
            price = this@toSeed[SeedTable.price]
            itemId = this@toSeed[SeedTable.id]
            desc = this@toSeed[SeedTable.desc]
        }
    }
}