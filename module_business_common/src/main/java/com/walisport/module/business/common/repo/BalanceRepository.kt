package com.walisport.module.business.common.repo

import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.CurrencyConfigDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.UserDataDao
import arch.cayenne.lib.database.entity.AvatarEmbedded
import arch.cayenne.lib.database.entity.CurrencyBean
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.database.entity.WalletBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IAccount
import arch.cayenne.lib.http.data.AccountInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class BalanceRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao,
    private val userDataDao: UserDataDao,
    private val currencyConfigDao: CurrencyConfigDao,
    private val manager: UserDataManager,
    private val httpClient: HttpClient
) : BaseRepository() {

    fun observeCurrency() = infoDao.observeCurrency().map { it ?: "" }

    fun observeInfo() = infoDao.observeInfo()

    suspend fun getCurrency(): String {
        return infoDao.getCurrency2() ?: "CNY"
    }

    fun setDefaultCurrency(ccy: String) {
        manager.setKeyValue(UserDataKey.KEY_DEFAULT_CURRENCY, ccy)
    }

    //设置以何种法币金额显示加密货币
    fun setFiatCurrency(ccy: String) {
        manager.setKeyValue(UserDataKey.KEY_DEFAULT_FIAT, ccy)
    }

    fun changeFiat(): UnPeekLiveData<Boolean> {
        val updateLiveData = UnPeekLiveData<Boolean>()
        scope.launch(Dispatchers.IO) {
            val api = httpClient.create(IAccount::class.java)
            httpClient.safeRequest(
                request = {
                    api.profileInfo()
                },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        launch {
                            saveCurrencyInfo(resp.data)
                            updateLiveData.postValue(true)
                        }
                    }
                },
                onFailure = { code, msg, throwable ->
                    "ProfileInfo failure, response------>$code,$msg,$throwable".loge(TAG)
                    updateLiveData.postValue(false)
                }
            )
        }
        return updateLiveData
    }

    private suspend fun saveCurrencyInfo(profileInfo: AccountInfo) {
        userDataDao.insert(
            UserDataBean(
                nickname = profileInfo.nickname,
                avatar = AvatarEmbedded(
                    url = profileInfo.avatar.url,
                    thumbhash = profileInfo.avatar.thumbhash
                ),
                Uid = 100L,
                registerTime = profileInfo.registerTime,
                vipLevel = profileInfo.vipLevel,
                score = profileInfo.score,
                ccy = profileInfo.ccy,
                list = profileInfo.list.map { WalletBean(it.ccy, it.score, it.exchangeScore) },
                admittedBetScore = profileInfo.admittedBetScore,
                requiredAdmittedBetScore = profileInfo.requiredAdmittedBetScore,
                vipStage = profileInfo.vipStage,
                nicknameChangeCount = profileInfo.nicknameChangeCount,
            )
        )
        infoDao.updateBalance(profileInfo.score)
    }


    private suspend fun mappingCurrency(user: UserDataBean?): Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>> {
        val currencyList = currencyConfigDao.getCurrencyConfigList()
        val fiat = arrayListOf<BaseCurrencyData.CurrencyContentData>()
        val crypto = arrayListOf<BaseCurrencyData.CurrencyContentData>()
        if (user == null) return Pair(fiat, crypto)
        val showAllCurrency = manager.getValue(UserDataKey.KEY_SHOW_ALL_CURRENCY, false)//先暫時為false
        val exchangeAmountUnit =
            currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_FIAT) }?.unit
                ?: "$"
        val currentSelectedCCY =
            currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.ccy
                ?: "USD"
        val currentSelectedFiat =
            currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_FIAT) }?.ccy
                ?: "USD"
        currencyList.forEach { currency ->
            val wallet = user.list.find { it.currency == currency.ccy }
            if (!currency.crypto) {
                fiat.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0L,
                        currency.scale,
                        null,  //法幣不需要匯率轉換,
                        "",
                        currentSelectedCCY,
                        currentSelectedFiat
                    )
                )
            } else {
                crypto.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0L,
                        currency.scale,
                        wallet?.convertedAmount,
                        exchangeAmountUnit,
                        currentSelectedCCY,
                        currentSelectedFiat
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
                    amountStr = "0.00",
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
                    amountStr = "0.00",
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
        scale: Long,
        exchangeAmount: Long?,
        exchangeAmountUnit: String,
        currencySelectedCCY: String,
        currentSelectedFiat: String,
    ): BaseCurrencyData.CurrencyContentData {
        return BaseCurrencyData.CurrencyContentData(
            id = id,
            icon = icon,
            ccy = ccy,
            currencyName = name,
            amount = amount,
            amountStr = if (crypto) amount.getFormalMoney(scale, false) else amount.getFormalMoney(
                scale,
                true
            ),
            exchangeAmount = if (exchangeAmount == null) "" else "$exchangeAmountUnit${
                exchangeAmount.getFormalMoney(
                    scale,
                    true
                )
            }",
            unit = unit,
            scale = scale.toInt(),
            isSelected = ccy == currencySelectedCCY,
            fiatSelected = ccy == currentSelectedFiat
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
        val showAllCurrency = manager.getValue(UserDataKey.KEY_SHOW_ALL_CURRENCY, false)//先暫時為false
        val exchangeAmountUnit =
            currencyList.find { it.ccy == manager.getValue<String>(UserDataKey.KEY_DEFAULT_CURRENCY) }?.unit
                ?: ""

        currencyList.forEach { currency ->
            val wallet = user.list.find { currency.ccy == it.currency }
            if (!currency.crypto) {
                fiat.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0L,
                        currency.scale,
                        null,
                        "",
                        "",
                        ""
                    )
                )
            } else {
                crypto.add(
                    currency.toCurrencyContentData2(
                        wallet?.balance ?: 0L,
                        currency.scale,
                        wallet?.convertedAmount,
                        exchangeAmountUnit,
                        "",
                        ""
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

    private fun keywordToSqlPattern(keyword: String): String {
        if (keyword.isEmpty()) return "%"
        return "%" + keyword.uppercase().map { "$it%" }.joinToString("")
    }

    private fun Long.getFormalMoney(scale: Long = 100, bl: Boolean): String {
        if (this == 0L)
            return "0.00"
        val value = this.toBigDecimal().divide(BigDecimal(scale))
        return if (bl) {
            value.setScale(2, RoundingMode.DOWN).toString()
        } else {
            if (isInteger(value)) {//小数点后无数字时，应该显示.00
                value.toPlainString() + ".00"
            } else {
                value.stripTrailingZeros().toPlainString()
            }
        }
    }

    private fun isInteger(value: BigDecimal): Boolean {
        return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0
    }
}