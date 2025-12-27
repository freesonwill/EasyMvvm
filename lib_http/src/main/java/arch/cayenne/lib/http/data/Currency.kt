package arch.cayenne.lib.http.data

data class CurrencyInfo(
    val id: Int,
    val name: String,
    val ccy: String,
    val crypto: Boolean,
    val scale: Long,
    val unit: String,
    val icon: String,
    val rate: Double
)