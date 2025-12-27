package arch.cayenne.lib.common.data.constants

import arch.cayenne.lib.common.R

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
            "KRW" -> "₩"
            "EUR" -> "€"
            "GBP" -> "£"
            else -> "$"
        }
    }

    fun getSymbolIcon(currency: String): Int? {
        return when (currency) {
            "USDT" -> R.drawable.ic_usdt
            "BTC" -> R.drawable.ic_btc
            "ETH" -> R.drawable.ic_eth
            else -> null
        }
    }
}