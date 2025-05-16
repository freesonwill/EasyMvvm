package arch.cayenne.module.home.utils

import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.MatchWithMarkets

/**
 * 找出投注單中未投注的selection，把它設為點擊狀態
 * */
suspend fun List<MatchWithMarkets>.setSelected(betDao: BetDao): List<MatchWithMarkets> {
    this.forEach { match ->
        match.setSelected(betDao)
    }
    return this
}

suspend fun MatchWithMarkets.setSelected(betDao: BetDao): MatchWithMarkets {
    val betSelections = betDao.getCurrentSelectionIds().toSet()  //在投注單內的內容
    this.markets.forEach { market ->
        market.selections.forEach {
            it.isSelected = betSelections.contains(it.selectionId)
        }
    }
    return this
}