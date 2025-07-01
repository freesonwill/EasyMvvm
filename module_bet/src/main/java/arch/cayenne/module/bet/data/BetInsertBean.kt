package arch.cayenne.module.bet.data

import arch.cayenne.lib.database.entity.BetSelectionBean

data class BetInsertBean(
    val matchId: Long,
    val marketId: Long,
    val marketName: String, // 盘口名称 ex. 讓分盤
    val selectionId: Long, // 盘口ID
    val name: String, // 盘口名称 ex. 中國 (+1.5)
    var odds: Int, // 盘口赔率 ex. 1.9
    val leagueName: String, // 联赛名称 ex. 世界盃
    val matchName: String, // 赛事名称 ex. 中國 vs 日本
    var isActive: Boolean, // 是否停止下注
    var isPlaying: Boolean, // 是否滾球
    var isParlay: Boolean,
    val provider: Int = 0  // 提供商ID
) {
    fun toBetSelectionBean(betId: Long): BetSelectionBean {
        return BetSelectionBean(
            betId = betId,
            matchId = matchId,
            marketId = marketId,
            marketName = marketName,
            selectionId = selectionId,
            name = name,
            odds = odds,
            leagueName = leagueName,
            matchName = matchName,
            isActive = isActive,
            isPlaying = isPlaying,
            isParlay = isParlay,
            provider = provider
        )
    }
}
