package arch.cayenne.lib.common.data.constants

import androidx.annotation.DrawableRes

sealed class BaseCurrencyData {
    data class CurrencyTitleData(
        val title: String
    ) : BaseCurrencyData()
    @Deprecated("這個是Mock資料，應該最後應該改為CurrencyContentData2")
    data class CurrencyContentData(
        @DrawableRes val icon: Int,
        val currency: String,
        val amount: String
    ) : BaseCurrencyData()

    data class CurrencyContentData2(
        val id: Int,
        val icon: String,
        val currencyName: String,
        val amount: String,
        val unit: String
    ) : BaseCurrencyData()
}