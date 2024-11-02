package com.lanier.game.feature.farm.market

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.BaseItem
import com.lanier.game.model.dto.MarketAddReqDTOModel
import com.lanier.game.model.dto.MarketRespDTOModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Desc:
 * Author: 幻弦让叶
 * Date 2024/9/28 19:00
 */
class MarketDaoImpl : MarketDao {
    override suspend fun getProductById(id: Int): MarketRespDTOModel? {
        return DatabaseFactory.process {
            transaction {
                val row = MarketTable
                    .selectAll()
                    .where { MarketTable.id eq id }
                    .singleOrNull()

                if (row == null) return@transaction null
                row.toMarketItem()
            }
        }
    }

    override suspend fun getAllProductsByType(
        type: Int,
        offset: Int,
        limit: Int,
    ): List<MarketRespDTOModel>? {
        return DatabaseFactory.process {
            if (BaseItem.validType(type).not()) {
                return@process null
            }
            val resultRows = transaction {
                MarketTable
                    .selectAll()
                    .where { MarketTable.itemType eq type }
                    .limit(limit, offset.toLong())
                    .toList()
            }
            if (resultRows.isEmpty()) return@process emptyList<MarketRespDTOModel>()
            resultRows.map { row ->
                row.toMarketItem()
            }
        }
    }

    override suspend fun getAllListedProductsByType(
        type: Int,
        offset: Int,
        limit: Int,
        isListed: Boolean
    ): List<MarketRespDTOModel>? {
        return DatabaseFactory.process {
            if (BaseItem.validType(type).not()) {
                return@process null
            }
            val resultRows = transaction {
                MarketTable
                    .selectAll()
                    .where { MarketTable.itemType eq type }
                    .andWhere { MarketTable.isListed eq isListed }
                    .limit(limit, offset.toLong())
                    .toList()
            }
            if (resultRows.isEmpty()) return@process emptyList<MarketRespDTOModel>()
            resultRows.map { row ->
                row.toMarketItem()
            }
        }
    }

    override suspend fun upsertProduct(model: MarketAddReqDTOModel): Boolean? {
        return DatabaseFactory.process {
            transaction {
                if (model.id == null) {
                    val resultRow = MarketTable.insert { statement ->
                        statement[itemType] = model.itemType
                        statement[refItemId] = model.itemId
                        statement[name] = model.name
                        statement[price] = model.price
                        statement[desc] = model.desc
                        statement[isListed] = model.isListed
                    }
                    val newId = resultRow[MarketTable.id]
                    newId > 0
                } else {
                    val result = MarketTable.update({ MarketTable.id eq model.id }) { statement ->
                        statement[MarketTable.itemType] = model.itemType
                        statement[MarketTable.refItemId] = model.itemId
                        statement[MarketTable.name] = model.name
                        statement[MarketTable.desc] = model.desc
                        statement[MarketTable.price] = model.price
                        statement[MarketTable.isListed] = model.isListed
                    }
                    result > 0
                }
            }
        }
    }

    override suspend fun upsertProducts(models: List<MarketAddReqDTOModel>): List<Int>? {
        return DatabaseFactory.process {
            transaction {
                val resultRows = MarketTable.batchInsert(
                    data = models,
                    ignore = true
                ) { model ->
                    this[MarketTable.name] = model.name
                    this[MarketTable.desc] = model.desc
                    this[MarketTable.price] = model.price
                    this[MarketTable.refItemId] = model.itemId
                    this[MarketTable.itemType] = model.itemType
                    this[MarketTable.isListed] = model.isListed
                }

                if (resultRows.isEmpty()) {
                    return@transaction null
                }

                val insertIds = resultRows.map {
                    it[MarketTable.id]
                }

                return@transaction insertIds
            }
        }
    }

    override suspend fun changeListedState(marketId: Int, listed: Boolean): Boolean? {
        return DatabaseFactory.process {
            transaction {
                val result = MarketTable.update({ MarketTable.id eq marketId }) { statement ->
                    statement[MarketTable.isListed] = isListed
                }
                result == 1
            }
        }
    }

    private fun ResultRow.toMarketItem(): MarketRespDTOModel {
        return MarketRespDTOModel(
            id = this[MarketTable.id],
            isListed = this[MarketTable.isListed],
            name = this@toMarketItem[MarketTable.name],
            price = this@toMarketItem[MarketTable.price],
            desc = this@toMarketItem[MarketTable.desc],
            itemId = this@toMarketItem[MarketTable.refItemId],
            itemType = this@toMarketItem[MarketTable.itemType],
        )
    }
}