package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.CodeException
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.reserveDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.OddsChangeEnum
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class SingleBetViewModel(
    private val betRepo: SingleBetRepository,
    private val balanceRepo: BalanceRepository
) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MediatorLiveData<BetSelectionBean>().apply {
        addSource(onNumberLimit) { number ->
            val min = number.first
            val max = number.second

            value?.let {
                if (_onComboMultiBetBeanListener.value != null) {
                    value = it.copy(isActive = max != 0L && min != 0L && originData!!.isActive)
                }
            }
        }
    }
    val onBetSheetListener: LiveData<BetSelectionBean> get() = _onBetSheetListener

    private val _onComboMultiBetBeanListener = MutableLiveData<ComboMultiBetBean>()
    val onComboMultiBetBeanListener: LiveData<ComboMultiBetBean> get() = _onComboMultiBetBeanListener

    private val _onBalanceListener = MutableLiveData<InfoBean?>()
    val onBalanceListener: LiveData<InfoBean?> get() = _onBalanceListener

    val balance: Long
        get() = _onBalanceListener.value?.balance ?: 0L

    private val _betTypeListener = MutableLiveData<BetTypeEnum?>()
    val betTypeListener: LiveData<BetTypeEnum?> get() = _betTypeListener

    private val _onReserveOddsListener = MutableLiveData<Int?>()
    val onReserveOddsListener: LiveData<Int?> get() = _onReserveOddsListener

    val moneySymbol: String
        get() = CurrencySymbols.getSymbol(_onBalanceListener.value?.currency ?: "")
    private val _sendBetting = MutableLiveData(false)
    val sendBetting:LiveData<Boolean> = _sendBetting

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 100
        fun getOdds(): String {
            return odds.getDisplayOdds()
        }

        fun setMoney() {
            val money = editValue
            val cMoney = if (money.isEmpty()) {
                ""
            } else if (money.last() == '.') {
                money.substring(0, money.length - 1)
            } else {
                money
            }
            value = if (cMoney.isEmpty()) {
                ""
            } else {

                fun shouldUseScientificNotation(number: BigDecimal): Boolean {
                    val plainString = number.toPlainString()
                    // 計算有效數字位數（不包括小數點和負號）
                    val digitsOnly = plainString.replace(".", "")
                    return digitsOnly.length > 16
                }
                val v = BigDecimal(editValue).multiply(BigDecimal(getOdds()))
                val finalResult = v.setScale(
                    2,
                    RoundingMode.DOWN
                ).stripTrailingZeros()

                val result = if (shouldUseScientificNotation(finalResult)) {
                    val mc = MathContext(16, RoundingMode.DOWN)
                    finalResult.round(mc).toString()
                } else {
                    finalResult.toPlainString()
                }
                result
            }
        }
        addSource(_onBetSheetListener) { data ->
            if (_onReserveOddsListener.value == null) {
                odds = data.odds
                setMoney()
            }
        }
        addSource(_onReserveOddsListener) { reserveOdds ->
            if (reserveOdds == null) {
                _onBetSheetListener.value?.let {
                    odds = it.odds
                }
            } else {
                odds = reserveOdds
            }
            setMoney()
        }
        addSource(onEditNumber) {
            setMoney()
        }
    }
    val onBetWinMoney: LiveData<String> get() = _onBetWinMoney
    private var originData: BetSelectionBean? = null

    private val _networkConnectedEvent = MutableLiveData<Event<DataState>>()
    val networkConnectedEvent: LiveData<Event<DataState>> get() = _networkConnectedEvent

    private val _oddsChangeListener = MutableLiveData<OddsChangeEnum>()
    val oddsChangeListener: LiveData<OddsChangeEnum> get() = _oddsChangeListener


    init {
        viewModelScope.launch {
            launch {
                betRepo.observeSelectionBean().distinctUntilChanged().collect {
                    setBetSheet(it)
                }
            }
            launch {
                betRepo.observeComboBean().collect {
                    _onComboMultiBetBeanListener.value = it
                    setNumberLimit(it.minAmount, it.maxAmount)
                }
            }
            launch {
                balanceRepo.observeInfo().collect {
                    _onBalanceListener.value = it
                }
            }
            launch {
                betRepo.observeBetType().collect {
                    _betTypeListener.value = it ?: BetTypeEnum.SINGLE
                }
            }
            launch {
                betRepo.observeOddsChange().collect {
                    _oddsChangeListener.value = it
                }
            }
        }
    }

    suspend fun sendBet(): Result<Unit> {
        if (!checkNetwork()) {
            return Result.failure(CodeException(-1, "Network Unavailable"))
        }
        val money = onEditNumber.value ?: return Result.failure(CodeException(-1, "Empty bet amount"))
        val oddsChange = _oddsChangeListener.value ?: return Result.failure(CodeException(-1, "OddsChange is null"))
        val currentOdds = _onBetSheetListener.value?.odds ?: 0
        val reserveOdds = _onReserveOddsListener.value?.reserveDisplayOdds()

        _sendBetting.value = true
        val result: Result<Unit> = let {
            val amount = money.toMoney()
            if (reserveOdds == null || reserveOdds == currentOdds) {
                val isSuccess = betRepo.saveToSingle()
                if (isSuccess.isFailure) return@let isSuccess
                betRepo.sendBet(amount, oddsChange)
            } else {
                val isSuccess = betRepo.saveToReserve(reserveOdds, amount)
                if (isSuccess.isFailure) return@let isSuccess
                betRepo.sendReserve(amount)
            }
        }

        if(result.isFailure) {
            _sendBetting.value = false
            "sendBet failure: ${result.exceptionOrNull()?.message}".loge(TAG)
        } else {
            //成功时会跳转，延迟设置，否则会看到残影
            viewModelScope.launch {
                delay(200)
                _sendBetting.value = false
            }
        }
        return result
    }
    fun removeBet() {
        betRepo.removeBet()
    }

    fun removeReserve() {
        _onReserveOddsListener.value = null
    }

    suspend fun saveToCombo() = betRepo.saveToCombo()

    fun saveToSingle() {
        viewModelScope.launch {
            betRepo.saveToSingle()
        }
    }

    fun saveToReserve(odds: Int) {
        _onReserveOddsListener.value = odds
    }

    private fun setBetSheet(bet: BetSelectionBean) {
        originData = bet.copy()
        if (_onComboMultiBetBeanListener.value == null) {
            _onBetSheetListener.value = bet
        } else {
            val number = onNumberLimit.value
            val min = number?.first ?: 0L
            val max = number?.second ?: 0L

            bet.isActive = max != 0L && min != 0L && bet.isActive
            _onBetSheetListener.value = bet
        }
    }

    private fun checkNetwork(): Boolean {
        if (!betRepo.isConnected) {
            _networkConnectedEvent.value = Event(DataState.NetworkUnavailable)
            return false
        }
        return true
    }

    fun checkOddsPass(): Boolean {
        val oddsChange = _oddsChangeListener.value ?: return false
        if (oddsChange == OddsChangeEnum.ANY) {
            return true
        }
        val initialOdds = _onBetSheetListener.value?.initialOdds ?: return false
        val currentOdds = _onBetSheetListener.value?.odds ?: return false
        if (oddsChange == OddsChangeEnum.NO_CHANGE && initialOdds != currentOdds) {
            return false
        }
        if (oddsChange == OddsChangeEnum.BETTER && currentOdds < initialOdds) {
            return false
        }
        return true
    }

    suspend fun mockBetData(){
        val data = _onBetSheetListener.value
        data?.isActive = false
        _onBetSheetListener.value = data
        delay(1000)
        data?.isActive = true
        _onBetSheetListener.value = data
    }
}