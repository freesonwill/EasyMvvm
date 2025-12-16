package arch.cayenne.lib.http.data

data class CurrencyInfo(
    val id: Int,
    val virtual: Boolean,
    val rate: Double,
    val unit: String,
    val name: String,
    val ccy: String,
    val icon: String,
)