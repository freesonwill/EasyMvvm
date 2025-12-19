package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.database.dao.CurrencyConfigDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.UserDataDao
import arch.cayenne.lib.database.entity.CurrencyBean
import arch.cayenne.lib.database.entity.UserDataBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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

    private suspend fun mappingCurrency(user: UserDataBean?) : Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val currencyList = currencyConfigDao.getCurrencyConfigList()
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        if (user == null) return Pair(fiat, crypto)
        val showAllCurrency =  manager.getValue(UserDataKey.KEY_SHOW_ALL_CURRENCY, false)//先暫時為false
        val exchangeAmountUnit = currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.unit ?: ""
        currencyList.forEach { currency ->
            val wallet = user.list.find { it.currency == currency.ccy }
            if (!currency.virtual) {
                fiat.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0.0,
                        null,  //法幣不需要匯率轉換,
                        "",
                    )
                )
            } else {
                crypto.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0.0,
                        wallet?.convertedAmount,
                        exchangeAmountUnit,
                    )
                )
            }
        }
        if (!showAllCurrency) {
            fiat.removeIf { it.amount == 0.0 }
            crypto.removeIf { it.amount == 0.0 }
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
            return@combine BaseCurrencyData.CurrencyContentData2(
                id = 0,
                icon = "",
                ccy = "",
                currencyName = "",
                amount = 0.0,
                amountStr = 0.0.getFormalMoney(),
                exchangeAmount = "",
                unit = ""
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
            ?: BaseCurrencyData.CurrencyContentData2(
                id = 0,
                icon = "",
                ccy = "",
                currencyName = "",
                amount = 0.0,
                amountStr = 0.0.getFormalMoney(),
                exchangeAmount = "",
                unit = ""
            )
    }

    suspend fun getUserCurrency(): Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val user = userDataDao.getUser()
        return mappingCurrency(user)

    }

    private fun CurrencyBean.toCurrencyContentData2(
        amount: Double,
        exchangeAmount: Long?,
        exchangeAmountUnit: String
    ): BaseCurrencyData.CurrencyContentData2 {
        return BaseCurrencyData.CurrencyContentData2(
            id = id,
            icon = icon,
            ccy = ccy,
            currencyName = name,
            amount = amount,
            amountStr = amount.getFormalMoney(),
            exchangeAmount = if(exchangeAmount == null) "" else "$exchangeAmountUnit${exchangeAmount.getMoney()}",
            unit = unit
        )
    }

    suspend fun search(keyword: String): Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val pattern = keywordToSqlPattern(keyword)
        val firstChar = if (keyword.isNotEmpty()) keyword.first().toString() else ""
        val user = userDataDao.getUser()
        val currencyList = currencyConfigDao.searchByKeyword(pattern, firstChar)
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        if (user == null) return Pair(fiat, crypto)
        val showAllCurrency =  manager.getValue(UserDataKey.KEY_SHOW_ALL_CURRENCY, false)//先暫時為false
        val exchangeAmountUnit = currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.unit ?: ""

        currencyList.forEach { currency ->
            val wallet = user.list.find { currency.ccy == it.currency }
            if (!currency.virtual) {
                fiat.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?:0.0,
                        null,
                        "",
                    )
                )
            } else {
                crypto.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?:0.0,
                        wallet?.convertedAmount,
                        exchangeAmountUnit,
                    )
                )
            }
        }
        if (!showAllCurrency) {
            fiat.removeIf { it.amount == 0.0 }
            crypto.removeIf { it.amount == 0.0 }
        }

        return Pair(fiat, crypto)
    }

    fun keywordToSqlPattern(keyword: String): String {
        if (keyword.isEmpty()) return "%"
        return "%" + keyword.uppercase().map { "$it%" }.joinToString("")
    }
}