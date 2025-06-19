package arch.cayenne.lib.common.data.constants

/**
 *
 * @date: 2025/6/3 14:59
 * @description: 货币符号
 */
object CurrencySymbols {

    fun getSymbol(currency: String): String {
        return when (currency) {
            "CNY" -> "¥"
            "USD" -> "$"
            "JPY" -> "¥"
            "EUR" -> "€"
            "GBP" -> "£"
            else -> ""
        }
    }
}