package arch.cayenne.lib.common.data.constants

import androidx.annotation.DrawableRes

sealed class BaseCurrencyData {
    data class CurrencyTitleData(
        val title: String
    ) : BaseCurrencyData()
    data class CurrencyContentData(
        @DrawableRes val icon: Int,
        val currency: String,
        val amount: String
    ) : BaseCurrencyData()
}