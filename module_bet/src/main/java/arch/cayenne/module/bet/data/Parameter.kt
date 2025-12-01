package arch.cayenne.module.bet.data

data class Parameter(
    val title: String,
    val titleTips: String,
    val items: List<ParameterItems>
)

data class ParameterItems(
    val title: String,
    val items: List<ParameterItems2>
)

data class ParameterItems2(
    val combo: String,
    val money: String?,
    val winMoney: String?,
    val odds: String
)
