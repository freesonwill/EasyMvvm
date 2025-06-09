package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.common.data.constants.NumberOverEnum
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class SingleBetViewModel(private val betRepo: SingleBetRepository, private val balanceRepo: BalanceRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MediatorLiveData<BetSelectionBean>().apply {
        addSource(onNumberLimit) { number ->
            val min = number.first
            val max = number.second

            value?.let {
                value = it.copy(isActive = max != 0L && min != 0L && originData!!.isActive)
            }
        }
    }
    val onBetSheetListener: LiveData<BetSelectionBean> get() =  _onBetSheetListener

    private val _onBalanceListener = MutableLiveData<Long>()
    val onBalanceListener: LiveData<Long> get() = _onBalanceListener

    private val _moneySymbolListener = MutableLiveData(CurrencySymbols.CNY)

    private val _betTypeListener = MutableLiveData<BetTypeEnum?>()
    val betTypeListener: LiveData<BetTypeEnum?> get() = _betTypeListener

    private val _onReserveOddsListener = MutableLiveData<Int?>()
    val onReserveOddsListener: LiveData<Int?> get() = _onReserveOddsListener

    val moneySymbolListener: LiveData<String> get() = _moneySymbolListener
    val moneySymbol: String
        get() = _moneySymbolListener.value ?: CurrencySymbols.CNY

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 1
        addSource(_onBetSheetListener) { data ->
            if (_onReserveOddsListener.value == null) {
                odds = data.odds
                value = editValue.toMoney().getMoney(odds)
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
            value = editValue.toMoney().getMoney(odds)
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
               money.toMoney().getMoney(odds)
            }
        }
    }
    val onBetWinMoney: LiveData<String> get() = _onBetWinMoney
    private var originData: BetSelectionBean? = null

    init {
        viewModelScope.launch {
            launch {
                betRepo.observeSelectionBean().collect {
                    setBetSheet(it)
                }
            }
            launch {
                betRepo.observeComboBean().collect {
                    setNumberLimit(it.minAmount, it.maxAmount)
                    val oriData = onEditNumber.value
                    if (oriData.isNullOrEmpty()) {
                        val balance = balanceRepo.getBalance()
                        if (it.inputMoney > balance) {
                            it.inputMoney = 0L
                            setOverNumberListener(NumberOverEnum.OVER_REMAINING)
                        } else if (it.inputMoney > it.maxAmount) {
                            it.inputMoney = it.maxAmount
                        }
                        if (it.inputMoney > 0L) {
                            setEditNumber(it.inputMoney)
                        }
                    } else {
                        val curMoney = oriData.toMoney()
                        if (curMoney > 0L) {
                            setEditNumber(curMoney)
                        }
                    }
                }
            }
            launch {
                balanceRepo.observeBalance().collect {
                    _onBalanceListener.value = it
                    setRemainingNumber(it)
                }
            }
            launch {
                betRepo.observeBetType().collect {
                    _betTypeListener.value = it
                    if (it == BetTypeEnum.RESERVE) {
                        _onReserveOddsListener.value = betRepo.getReserveOdds()
                    }
                }
            }
        }
    }

    fun sendBet() {
        val money = onEditNumber.value?.toMoney() ?: return
        val type = _betTypeListener.value ?: return
        if (type == BetTypeEnum.SINGLE) {
            betRepo.sendBet(money)
        } else if (type == BetTypeEnum.RESERVE) {
            betRepo.sendReserve(money)
        }
    }

    fun removeBet() {
        betRepo.removeBet()
    }

    fun removeReserve() {
        betRepo.removeReserve()
        _onReserveOddsListener.value = null
    }

    fun saveToCombo() {
        onEditNumber.value?.let {
            if (it.isEmpty()) return@let
            val money = it.toMoney()
            betRepo.saveInputMoney(money)
        }
        betRepo.saveToCombo()
    }

    fun saveToReserve(odds: Int) {
        betRepo.saveToReserve(odds)
    }

    private fun setBetSheet(bet: BetSelectionBean) {
        originData = bet.copy()
        val number = onNumberLimit.value
        val min = number?.first ?: 0L
        val max = number?.second ?: 0L

        bet.isActive = max != 0L && min != 0L && bet.isActive
        _onBetSheetListener.value = bet
    }
}