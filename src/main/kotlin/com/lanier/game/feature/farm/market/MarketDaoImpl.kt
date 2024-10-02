package com.lanier.game.feature.farm.market

import com.lanier.game.DatabaseFactory
import com.lanier.game.model.BaseItem
import com.lanier.game.model.dto.MarketAddReqDTOModel
import com.lanier.game.model.dto.MarketRespDTOModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
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

    override suspend fun getAllProductsByType(type: Int): List<MarketRespDTOModel>? {
        return DatabaseFactory.process {
            if (BaseItem.validType(type).not()) {
                return@process null
            }
            val resultRows = transaction {
                MarketTable
                    .selectAll()
                    .where { MarketTable.itemType eq type }
                    .toList()
            }
            if (resultRows.isEmpty()) return@process emptyList<MarketRespDTOModel>()
            resultRows.map { row ->
                row.toMarketItem()
            }
        }
    }

    override suspend fun addProduct(model: MarketAddReqDTOModel): Boolean? {
        return DatabaseFactory.process {
            transaction {
                val resultRow = MarketTable.insert { statement ->
                    statement[itemType] = model.typeId
                    statement[refItemId] = model.itemId
                    statement[name] = model.name
                    statement[price] = model.price
                    statement[desc] = model.desc
                    statement[isListed] = model.isListed
                }
                val newId = resultRow[MarketTable.id]
                newId > 0
            }
        }
    }

    private fun ResultRow.toMarketItem(): MarketRespDTOModel {
        return MarketRespDTOModel(
            id = this[MarketTable.id],
            isListed = this[MarketTable.isListed],
        ).apply {
            name = this@toMarketItem[MarketTable.name]
            price = this@toMarketItem[MarketTable.price]
            desc = this@toMarketItem[MarketTable.desc]
            itemId = this@toMarketItem[MarketTable.refItemId]
            itemType = this@toMarketItem[MarketTable.itemType]
        }
    }
}