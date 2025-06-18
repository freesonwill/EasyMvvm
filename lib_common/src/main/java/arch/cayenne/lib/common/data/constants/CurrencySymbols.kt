package arch.cayenne.lib.common.data.constants

/**
 *
 * @date: 2025/6/3 14:59
 * @description: 货币符号
 */
enum class CurrencySymbols(val currency: String, val symbol: String) {
    CNY("CNY", "¥"),
    USD("USD", "$"),
    JPY("JPY", "¥"),
    EUR("EUR", "€"),
    GBP("GBP", "£"),
    ;

    companion object {
        fun getSymbol(currency: String): String =
            entries.find { it.currency == currency }?.symbol ?: ""
    }
}