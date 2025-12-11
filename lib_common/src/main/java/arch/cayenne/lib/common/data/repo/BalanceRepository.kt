package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.dao.CurrencyConfigDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.UserDataDao
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

    suspend fun getCurrency(): String? {
        return infoDao.getCurrency()
    }

    suspend fun getUserCurrency(): Pair<List<BaseCurrencyData.CurrencyContentData2>, List<BaseCurrencyData.CurrencyContentData2>> {
        val user = userDataDao.getUser()
        val currencyList = currencyConfigDao.getCurrencyConfigList()
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData2>()
        user.balanceWallet.forEach {
            val currency = currencyList.find { currency -> currency.id == it.key }
            if (currency != null) {
                if (!currency.virtual) {
                    fiat.add(
                        BaseCurrencyData.CurrencyContentData2(
                            icon = "",
                            currencyName = currency.name,
                            amount = it.value.getFormalMoney(),
                            unit = currency.unit
                        )
                    )
                } else {
                    crypto.add(
                        BaseCurrencyData.CurrencyContentData2(
                            icon = "",
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
}