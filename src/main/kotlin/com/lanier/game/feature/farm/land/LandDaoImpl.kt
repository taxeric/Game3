package com.lanier.game.feature.farm.land

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.dto.Land
import com.lanier.game.model.LandTable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by 幻弦让叶
 * Date 2024/9/23 22:39
 */
class LandDaoImpl : LandDao {

    override suspend fun registerLandForUser(userId: Int): List<Int>? {
        return DatabaseFactory.process {
            val insertData = mutableListOf<Land>()
            repeat(16) { index ->
                insertData.add(
                    Land(
                        userId = userId,
                        status = if (index <= 2) Land.IDLE else Land.UNLOCK,
                    )
                )
            }
            transaction {
                val resultRows = LandTable.batchInsert(
                    data = insertData,
                    ignore = true,
                    shouldReturnGeneratedValues = true
                ) { value ->
                    this[LandTable.userId] = value.userId
                    this[LandTable.cropId] = value.cropId
                    this[LandTable.toNextStageRemainingTime] = value.nextStageRemainTime
                    this[LandTable.lastHarvestTime] = value.lastHarvestTime
                    this[LandTable.maxHarvestCount] = value.maxHarvestCount
                    this[LandTable.harvestCount] = value.harvestCount
                    this[LandTable.status] = value.status
                }

                if (resultRows.isEmpty()) {
                    return@transaction null
                }

                val insertIds = resultRows.map { row ->
                    row[LandTable.id]
                }
                return@transaction insertIds
            }
        }
    }

    override suspend fun getLandsInfoByUid(userId: Int): List<Land>? {
        return DatabaseFactory.process {
            val resultRows = transaction {
                LandTable.selectAll()
                    .where {
                        LandTable.userId eq userId
                    }
                    .toList()
            }
            if (resultRows.isEmpty()) return@process null

            val lands = resultRows.map { row ->
                Land(
                    landId = row[LandTable.id],
                    userId = row[LandTable.userId],
                    cropId = row[LandTable.cropId],
                    nextStageRemainTime = row[LandTable.toNextStageRemainingTime],
                    lastHarvestTime = row[LandTable.lastHarvestTime],
                    maxHarvestCount = row[LandTable.maxHarvestCount],
                    harvestCount = row[LandTable.harvestCount],
                    status = row[LandTable.status],
                )
            }
            lands
        }
    }
}