package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.dao.CurrencyConfigDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.UserDataDao
import arch.cayenne.lib.database.entity.UserDataBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map

class BalanceRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao,
    private val userDataDao: UserDataDao,
    private val currencyConfigDao: CurrencyConfigDao,
): BaseRepository() {

    fun observeBalance() = userDataDao.observeBalance().map { it ?: 0L }

    fun observeCurrency() = infoDao.observeCurrency().map { it?: "" }

    fun observeInfo() = infoDao.observeInfo()

    suspend fun getBalance(): Long {
        return infoDao.getBalance()
    }

    suspend fun getCurrency(): String {
        return infoDao.getCurrency()
    }

    private suspend fun mappingCurrency(user: UserDataBean?) : Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val currencyList = currencyConfigDao.getCurrencyConfigList()
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        if (user == null) return Pair(fiat, crypto)

        user.balanceWallet.forEach {
            val currency = currencyList.find { currency -> currency.ccy == it.key }
            if (currency != null) {
                if (!currency.virtual) {
                    fiat.add(
                        BaseCurrencyData.CurrencyContentData2(
                            id = currency.id,
                            icon = currency.icon,
                            currencyName = currency.name,
                            amount = it.value.getFormalMoney(),
                            unit = currency.unit
                        )
                    )
                } else {
                    crypto.add(
                        BaseCurrencyData.CurrencyContentData2(
                            id = currency.id,
                            icon = currency.icon,
                            currencyName = currency.name,
                            amount = it.value.getFormalMoney(),
                            unit = currency.unit
                        )
                    )
                }
            }
        }
        return Pair(fiat, crypto)
    }

    fun observeUserCurrency() = userDataDao.observeUser().map {
        mappingCurrency(it)
    }

    suspend fun getUserCurrency(): Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val user = userDataDao.getUser()
        return mappingCurrency(user)

    }

    suspend fun search(keyword: String): Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val pattern = keywordToSqlPattern(keyword)
        val firstChar = if (keyword.isNotEmpty()) keyword.first().toString() else ""
        val user = userDataDao.getUser()
        val currencyList = currencyConfigDao.searchByKeyword(pattern, firstChar)
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        if (user == null) return Pair(fiat, crypto)

        currencyList.forEach {
            if (user.balanceWallet.contains(it.ccy)) {
                if (!it.virtual) {
                    fiat.add(
                        BaseCurrencyData.CurrencyContentData2(
                            id = it.id,
                            icon = it.icon,
                            currencyName = it.name,
                            amount = user.balanceWallet[it.ccy]!!.getFormalMoney(),
                            unit = it.unit
                        )
                    )
                } else {
                    crypto.add(
                        BaseCurrencyData.CurrencyContentData2(
                            id = it.id,
                            icon = it.icon,
                            currencyName = it.name,
                            amount = user.balanceWallet[it.ccy]!!.getFormalMoney(),
                            unit = it.unit
                        )
                    )
                }
            }
        }
        return Pair(fiat, crypto)
    }

    fun keywordToSqlPattern(keyword: String): String {
        if (keyword.isEmpty()) return "%"
        return "%" + keyword.uppercase().map { "$it%" }.joinToString("")
    }
}