package com.lanier.game.feature.farm.warehouse

import com.lanier.game.DatabaseFactory
import com.lanier.game.feature.farm.crop.CropTable
import com.lanier.game.feature.farm.seed.SeedTable
import com.lanier.game.model.BaseItem
import com.lanier.game.model.dto.CropRespDTOModel
import com.lanier.game.model.dto.SeedRespDTOModel
import com.lanier.game.model.dto.WarehouseRespDTOModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Desc: 仓库实现
 * Author: 幻弦让叶
 * Date 2024/9/26 23:13
 */
class WarehouseDaoImpl : WarehouseDao {
    override suspend fun increase(userId: Int, itemType: Int, itemId: Int, quantity: Int): Boolean? {
        return DatabaseFactory.process {
            return@process transaction {
                val itemRow = obtainWarehouseItem(userId, itemType, itemId)

                if (itemRow == null) {
                    WarehouseTable.insert { statement ->
                        statement[WarehouseTable.userId] = userId
                        statement[WarehouseTable.itemType] = itemType
                        statement[WarehouseTable.itemId] = itemId
                        statement[WarehouseTable.quantity] = quantity
                    }

                    true
                } else {
                    val warehouseId = itemRow[WarehouseTable.id]
                    var tempQuantity = itemRow[WarehouseTable.quantity]
                    tempQuantity += quantity
                    WarehouseTable.update({ WarehouseTable.id eq warehouseId }) { statement ->
                        statement[WarehouseTable.quantity] = tempQuantity
                    }

                    true
                }
            }
        }
    }

    override suspend fun consume(
        userId: Int,
        itemType: Int,
        itemId: Int,
        quantity: Int,
    ): Boolean? {
        return DatabaseFactory.process {
            return@process transaction {
                val itemRow = obtainWarehouseItem(userId, itemType, itemId) ?: return@transaction false

                var tempQuantity = itemRow[WarehouseTable.quantity]
                tempQuantity -= quantity
                if (tempQuantity <= 0) {
                    tempQuantity = 0
                }
                val warehouseId = itemRow[WarehouseTable.id]
                WarehouseTable.update({ WarehouseTable.id eq warehouseId }) { statement ->
                    statement[WarehouseTable.quantity] = tempQuantity
                }
                true
            }
        }
    }

    override suspend fun getMerchandiseByType(type: Int): List<WarehouseRespDTOModel<BaseItem>>? {
        return DatabaseFactory.process {
            if (BaseItem.validType(type).not()) {
                return@process null
            }
            val table = obtainItemTableByType(type) ?: return@process null
            return@process when (table) {
                is SeedTable -> {
                    WarehouseTable
                        .innerJoin(SeedTable, { WarehouseTable.itemId }, { SeedTable.id })
                        .selectAll()
                        .where { WarehouseTable.itemType eq type }
                        .toList()
                        .map {
                            val seed = SeedRespDTOModel(
                                seedId = it[SeedTable.id],
                                cropId = it[SeedTable.cropId],
                                maxHarvestCount = it[SeedTable.maxHarvestCount],
                                cropExpPer = it[SeedTable.cropExpPer],
                                singleHarvestAmount = it[SeedTable.singleHarvestAmount],
                                season = it[SeedTable.season],
                                stageInfo = it[SeedTable.stageInfo],
                                plantLevel = it[SeedTable.plantLevel],
                                seedExp = it[SeedTable.seedExp],
                            ).apply {
                                name = it[SeedTable.name]
                                price = it[SeedTable.price]
                                desc = it[SeedTable.desc]
                            }
                            WarehouseRespDTOModel(
                                warehouseId = it[WarehouseTable.id],
                                quantity = it[WarehouseTable.quantity],
                                item = seed
                            )
                        }
                }

                is CropTable -> {
                    WarehouseTable
                        .innerJoin(SeedTable, { WarehouseTable.itemId }, { CropTable.id })
                        .selectAll()
                        .where { WarehouseTable.itemType eq type }
                        .toList()
                        .map {
                            val crop = CropRespDTOModel(
                                cropId = it[CropTable.id],
                                seedId = it[CropTable.seedId],
                                season = it[CropTable.season]
                            ).apply {
                                name = it[CropTable.name]
                                price = it[CropTable.price]
                                desc = ""
                            }
                            WarehouseRespDTOModel(
                                warehouseId = it[WarehouseTable.id],
                                quantity = it[WarehouseTable.quantity],
                                item = crop
                            )
                        }
                }

                else -> null
            }
        }
    }

    private fun Transaction.obtainWarehouseItem(
        userId: Int,
        itemType: Int,
        itemId: Int,
    ) : ResultRow? {
        return WarehouseTable.selectAll()
            .andWhere { WarehouseTable.userId eq userId }
            .andWhere { WarehouseTable.itemType eq itemType }
            .andWhere { WarehouseTable.itemId eq itemId }
            .singleOrNull()
    }

    private fun obtainItemTableByType(type: Int) : Table? {
        return when (type) {
            BaseItem.TYPE_SEED -> SeedTable
            BaseItem.TYPE_CROP -> CropTable
            else -> null
        }
    }
}