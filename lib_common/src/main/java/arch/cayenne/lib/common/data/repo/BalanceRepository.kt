package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.CurrencyConfigDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.UserDataDao
import arch.cayenne.lib.database.entity.CurrencyBean
import arch.cayenne.lib.database.entity.UserDataBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.math.BigDecimal
import java.math.RoundingMode

class BalanceRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao,
    private val userDataDao: UserDataDao,
    private val currencyConfigDao: CurrencyConfigDao,
    private val manager: UserDataManager,
): BaseRepository() {

    fun observeCurrency() = infoDao.observeCurrency().map { it?: "" }

    fun observeInfo() = infoDao.observeInfo()

    suspend fun getBalance(): Long {
        return infoDao.getBalance()
    }

    suspend fun getCurrency(): String {
        return infoDao.getCurrency2() ?: "CNY"
    }

    fun setDefaultCurrency(ccy: String) {
        manager.setKeyValue(UserDataKey.KEY_DEFAULT_CURRENCY, ccy)
    }


    private suspend fun mappingCurrency(user: UserDataBean?) : Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>> {
        val currencyList = currencyConfigDao.getCurrencyConfigList()
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData>()
        if (user == null) return Pair(fiat, crypto)
        val showAllCurrency =  manager.getValue(UserDataKey.KEY_SHOW_ALL_CURRENCY, false)//先暫時為false
        val exchangeAmountUnit = currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.unit ?: ""
        val currentSelectedCCY = currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.ccy ?: "USD"
        currencyList.forEach { currency ->
            val wallet = user.list.find { it.currency == currency.ccy }
            if (!currency.crypto) {
                fiat.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0L,
                        null,  //法幣不需要匯率轉換,
                        "",
                        currentSelectedCCY
                    )
                )
            } else {
                crypto.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0L,
                        wallet?.convertedAmount,
                        exchangeAmountUnit,
                        currentSelectedCCY
                    )
                )
            }
        }
        if (!showAllCurrency) {
            fiat.removeIf { it.amount == 0L }
            crypto.removeIf { it.amount == 0L }
        }
        return Pair(fiat, crypto)
    }

    fun observeUserCurrency() = userDataDao.observeUser()
        .combine(
            manager.observe<String?>(UserDataKey.KEY_DEFAULT_CURRENCY)
                .onStart {
                    emit(manager.getValue(UserDataKey.KEY_DEFAULT_CURRENCY))
                }
        ) { user, defaultCurrency ->
        // profile/info沒進資料庫
        if (user == null) {
            return@combine BaseCurrencyData.CurrencyContentData(
                id = 0,
                icon = "",
                ccy = "",
                currencyName = "",
                amount = 0L,
                amountStr = 0L.getFormalMoney(),
                exchangeAmount = "",
                unit = "",
                scale = 0
            )
        }

        val (fait, crypto) = mappingCurrency(user)
        //之前有紀錄預設顯示的錢包，並且在原本user的內容中
        if (defaultCurrency != null) {
            val currency = fait.find { it.ccy == defaultCurrency }
                ?: crypto.find { it.ccy == defaultCurrency }
            if (currency != null) {
                return@combine currency
            }
        }
        return@combine fait.firstOrNull()
            ?: crypto.firstOrNull()
            ?: BaseCurrencyData.CurrencyContentData(
                id = 0,
                icon = "",
                ccy = "",
                currencyName = "",
                amount = 0L,
                amountStr = 0L.getFormalMoney(),
                exchangeAmount = "",
                unit = "",
                scale = 0,
            )
    }

    suspend fun getUserCurrency(): Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>> {
        val user = userDataDao.getUser()
        return mappingCurrency(user)

    }

    private fun CurrencyBean.toCurrencyContentData2(
        amount: Long,
        exchangeAmount: Long?,
        exchangeAmountUnit: String,
        currencySelectedCCY: String,
    ): BaseCurrencyData.CurrencyContentData {
        return BaseCurrencyData.CurrencyContentData(
            id = id,
            icon = icon,
            ccy = ccy,
            currencyName = name,
            amount = amount,
            amountStr = if (this.crypto && this.ccy != "USDT") amount.getFormalMoney(1) else amount.getFormalMoney(), //TODO 以後會加上rate，根據不同的需求除不同的rate
            exchangeAmount = if(exchangeAmount == null) "" else "$exchangeAmountUnit${exchangeAmount.getFormalMoney()}",
            unit = unit,
            scale = if (this.crypto && this.ccy != "USDT") 0 else 2,
            isSelected = ccy == currencySelectedCCY,
        )
    }

    suspend fun search(keyword: String): Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>> {
        val pattern = keywordToSqlPattern(keyword)
        val firstChar = if (keyword.isNotEmpty()) keyword.first().toString() else ""
        val user = userDataDao.getUser()
        val currencyList = currencyConfigDao.searchByKeyword(pattern, firstChar)
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData>()
        if (user == null) return Pair(fiat, crypto)
        val showAllCurrency =  manager.getValue(UserDataKey.KEY_SHOW_ALL_CURRENCY, false)//先暫時為false
        val exchangeAmountUnit = currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.unit ?: ""

        currencyList.forEach { currency ->
            val wallet = user.list.find { currency.ccy == it.currency }
            if (!currency.crypto) {
                fiat.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?:0L,
                        null,
                        "",
                        "",
                    )
                )
            } else {
                crypto.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?:0L,
                        wallet?.convertedAmount,
                        exchangeAmountUnit,
                        "",
                    )
                )
            }
        }
        if (!showAllCurrency) {
            fiat.removeIf { it.amount == 0L }
            crypto.removeIf { it.amount == 0L }
        }

        return Pair(fiat, crypto)
    }

    fun keywordToSqlPattern(keyword: String): String {
        if (keyword.isEmpty()) return "%"
        return "%" + keyword.uppercase().map { "$it%" }.joinToString("")
    }

    fun Long.getFormalMoney(divisor: Int = 100): String {
        if (this == 0L) return "0.00"

        val value = this.toBigDecimal()
            .divide(BigDecimal(divisor), 8, RoundingMode.DOWN)

        // 是否為整數（小數部分 = 0）
        return if (value.stripTrailingZeros().scale() <= 0) {
            // 整數 → 補 .00
            value.setScale(2, RoundingMode.DOWN).toPlainString()
        } else {
            // 非整數 → 去掉多餘 0
            value.stripTrailingZeros().toPlainString()
        }
    }
}