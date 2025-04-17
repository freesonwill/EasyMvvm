package arch.cayenne.module.bet.data

data class ComboRateBean(
    val combo: Int,
    val odds: Int,
    var money: String = "",
)
