package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.common.utils.ext.SportMoneyOddsExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

    private val _onBalanceListener = MutableLiveData<InfoBean>()
    val onBalanceListener: LiveData<InfoBean> get() = _onBalanceListener

    private val _betTypeListener = MutableLiveData<BetTypeEnum?>()
    val betTypeListener: LiveData<BetTypeEnum?> get() = _betTypeListener

    private val _onReserveOddsListener = MutableLiveData<Int?>()
    val onReserveOddsListener: LiveData<Int?> get() = _onReserveOddsListener

    private val _onCanBetListener = MediatorLiveData(false).apply {
        val checkEligibility = {
            val betSheet = _onBetSheetListener.value
            val editNumber = onEditNumber.value
            val odds = _onReserveOddsListener.value

            value = if (betSheet == null || editNumber == null) {
                false
            } else {
                val money = editValue.toMoney()
                val isMoneyValid = minMoney != 0L && money >= minMoney
                val isBetSheetActive = betSheet.isActive

                if (odds == null) {
                    isBetSheetActive && isMoneyValid
                } else {
                    isBetSheetActive && isMoneyValid && odds >= betSheet.odds
                }
            }
        }

        addSource(_onBetSheetListener) { checkEligibility() }
        addSource(onEditNumber) { checkEligibility() }
        addSource(_onReserveOddsListener) { checkEligibility() }
    }
    val onCanBetListener: LiveData<Boolean> get() = _onCanBetListener

    val moneySymbol: String
        get() = CurrencySymbols.getSymbol(_onBalanceListener.value?.currency ?: "")

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 1
        addSource(_onBetSheetListener) { data ->
            if (_onReserveOddsListener.value == null) {
                odds = data.odds
                value = editValue.toMoney().getFormalMoney(odds)
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
            value = editValue.toMoney().getFormalMoney(odds)
        }
        addSource(onEditNumber) {
            val money = if (it.isEmpty()) {
                "0"
            } else if (it.last() == '.') {
                it.substring(0, it.length - 1)
            } else {
                it
            }
            value = if (money.isEmpty()) {
                "0.00"
            } else {
                money.toMoney().getFormalMoney(odds)
            }
        }
    }
    val onBetWinMoney: LiveData<String> get() = _onBetWinMoney
    private var originData: BetSelectionBean? = null

    private val _networkConnectedEvent = MutableLiveData<Event<DataState>>()
    val networkConnectedEvent: LiveData<Event<DataState>> get() = _networkConnectedEvent

    init {
        setNumberLimit(0L, 0L)
        viewModelScope.launch {
            launch {
                betRepo.observeSelectionBean().collect {
                    setBetSheet(it)
                }
            }
            launch {
                betRepo.observeComboBean().collect {
                    setComboMultiBet(it)
                    setNumberLimit(it.minAmount, it.maxAmount)
                }
            }
            launch {
                balanceRepo.observeBalance().collect {
                    _onBalanceListener.value = it
                    if (it != null) {
                        setRemainingNumber(it.balance)
                    }
                }
            }
            launch {
                betRepo.observeBetType().collect {
                    _betTypeListener.value = it ?: BetTypeEnum.SINGLE
                }
            }
        }
    }

    fun sendBet(): Boolean {
        if (!checkNetwork()) {
            return false
        }
        val money = onEditNumber.value?.toMoney() ?: return false
        val currentOdds = _onBetSheetListener.value?.odds ?: 0
        val reserveOdds = _onReserveOddsListener.value

        viewModelScope.launch {
            if (reserveOdds == null || reserveOdds == currentOdds) {
                val isSuccess = async {
                    betRepo.saveToSingle()
                }.await()
                if (isSuccess) {
                    betRepo.sendBet(money)
                }
            } else {
                val isSuccess = async {
                    betRepo.saveToReserve(reserveOdds)
                }.await()
                if (isSuccess) {
                    betRepo.sendReserve(money)
                }
            }
        }
        return true
    }

    fun removeBet() {
        betRepo.removeBet()
    }

    fun removeReserve() {
        _onReserveOddsListener.value = null
    }

    fun saveToCombo() {
        betRepo.saveToCombo()
    }

    fun saveToSingle() {
        viewModelScope.launch {
            betRepo.saveToSingle()
        }
    }

    fun saveToReserve(odds: Int) {
        _onReserveOddsListener.value = odds
    }

    private fun setComboMultiBet(data: ComboMultiBetBean) {
        val lastData = _onComboMultiBetBeanListener.value
        if (lastData != null) {
            data.inputMoney = if (lastData.inputMoney > data.maxAmount) {
                data.maxAmount
            } else {
                lastData.inputMoney
            }
        }
        _onComboMultiBetBeanListener.value = data
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
}