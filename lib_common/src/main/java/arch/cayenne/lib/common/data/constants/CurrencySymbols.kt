package arch.cayenne.lib.common.data.constants

import android.annotation.SuppressLint
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

    @SuppressLint("DefaultLocale")
    fun getFormatAmount(currency: String, amount: Float): String {
        return when (currency) {
            "CNY" -> getFormatCNY(amount)
            else -> getFormatUSD(amount)
        }
    }

    @SuppressLint("DefaultLocale")
    fun getFormatUSD(amount: Float): String {
        return when {
            amount >= 1_000_000_000 -> String.format("%.2fB", amount / 1_000_000_000f)
            amount >= 1_000_000 -> String.format("%.2fM", amount / 1_000_000f)
            amount >= 1_000 -> String.format("%.2fK", amount / 1_000f)
            else -> String.format("%.2f", amount)
        }
    }

    @SuppressLint("DefaultLocale")
    fun getFormatCNY(amount: Float): String {
        return when {
            amount >= 10_000 -> String.format("%.2fW", amount / 10_000)
            else -> String.format("%.2f元", amount)
        }
    }
}