package arch.cayenne.lib.common.data.constants

sealed class BaseCurrencyData {
    data class CurrencyTitleData(
        val title: String
    ) : BaseCurrencyData()

    data class CurrencyContentData(
        val id: Int,
        val ccy: String,
        val icon: String,
        val currencyName: String,
        val amount: Long,
        val amountStr: String,
        val exchangeAmount: String,
        val unit: String,
        val scale: Int = 2,
        val isSelected: Boolean = false,
    ) : BaseCurrencyData()
}