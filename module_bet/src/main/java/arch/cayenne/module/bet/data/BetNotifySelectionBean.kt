package arch.cayenne.module.bet.data

/**
 * 比赛盘口推送
 *
 * @property matchId
 * @property selectionId
 * @property odds
 * @property isActive
 * @property isParlay
 */
data class BetNotifySelectionBean(
    val matchId: Long,
    val selectionId: Long,
    val odds: Int,
    var isActive: Boolean,
    var isParlay: Boolean
)