package com.lanier.game.feature.farm.seed

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.SeedRespDTOModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
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

    private fun ResultRow.toSeed(): SeedRespDTOModel {
        return SeedRespDTOModel(
            seedId = this[SeedTable.id],
            cropId = this[SeedTable.cropId],
            maxHarvestCount = this[SeedTable.maxHarvestCount],
            singleHarvestAmount = this[SeedTable.singleHarvestAmount],
            singleHarvestExp = this[SeedTable.singleHarvestExp],
            season = this[SeedTable.season],
            stageInfo = this[SeedTable.stageInfo],
            plantLevel = this[SeedTable.plantLevel],
        ).apply {
            name = this@toSeed[SeedTable.name]
            price = this@toSeed[SeedTable.price]
        }
    }
}