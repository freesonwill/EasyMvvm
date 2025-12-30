package arch.cayenne.module.bet.data

import arch.cayenne.lib.database.entity.BetSelectionBean

//投注資料
data class BetInsertBean(
    val sportId: Int,
    val matchId: Long,
    val marketId: Long,
    val marketName: String, // 盘口名称 ex. 讓分盤
    val score: String, // 比分 ex. 1:0
    val selectionId: Long, // 盘口ID
    val name: String, // 盘口名称 ex. 中國 (+1.5)
    var odds: Int, // 盘口赔率 ex. 1.9
    val leagueName: String, // 联赛名称 ex. 世界盃
    val matchName: String, // 赛事名称 ex. 中國 vs 日本
    var isActive: Boolean, // 是否停止下注
    var isPlaying: Boolean, // 是否滾球
    var isParlay: Boolean, //是否串关
    val provider: Int  // 提供商ID
) {
    fun toBetSelectionBean(betId: Long): BetSelectionBean {
        return BetSelectionBean(
            betId = betId,
            sportId = sportId,
            matchId = matchId,
            marketId = marketId,
            marketName = marketName,
            score = score,
            selectionId = selectionId,
            name = name,
            odds = odds,
            initialOdds = odds,
            leagueName = leagueName,
            matchName = matchName,
            isActive = isActive,
            isPlaying = isPlaying,
            isParlay = isParlay,
            provider = provider
        )
    }
}
