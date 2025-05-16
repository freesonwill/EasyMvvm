package arch.cayenne.module.home.utils

import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.MatchWithMarkets

/**
 * 找出投注單中未投注的selection，把它設為點擊狀態
 * @param selectedIds : 自己可以帶已知的selection id ，這樣可以少一次query
 * */
suspend fun List<MatchWithMarkets>.setSelected(betDao: BetDao, selectedIds: List<Long>? = null): List<MatchWithMarkets> {
    this.forEach { match ->
        match.setSelected(betDao, selectedIds)
    }
    return this
}

suspend fun MatchWithMarkets.setSelected(betDao: BetDao, selectedIds: List<Long>? = null): MatchWithMarkets {
    val betSelections = selectedIds ?: betDao.getCurrentSelectionIds().toSet()  //在投注單內的內容
    this.markets.forEach { market ->
        market.selections.forEach {
            it.isSelected = betSelections.contains(it.selectionId)
        }
    }
    return this
}