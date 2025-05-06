package arch.cayenne.module.bet.data

data class BetNotifySelectionBean(
    val matchId: Long,
    val selectionId: Long,
    val odds: Int,
    var isActive: Boolean,
    var isParlay: Boolean
)