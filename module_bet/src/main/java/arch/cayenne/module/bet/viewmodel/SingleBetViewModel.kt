package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.data.NumberOverEnum
import arch.cayenne.module.bet.repo.BalanceRepository
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class SingleBetViewModel(private val betRepo: SingleBetRepository, private val balanceRepo: BalanceRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MutableLiveData<BetSelectionBean>()
    val onBetSheetListener: LiveData<BetSelectionBean> get() =  _onBetSheetListener

    private val _onBalanceListener = MutableLiveData<Long>()
    val onBalanceListener: LiveData<Long> get() = _onBalanceListener

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 1
        addSource(_onBetSheetListener) { data ->
            odds = data.odds
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

    init {
        viewModelScope.launch {
            launch {
                betRepo.observeSelectionBean().collect {
                    _onBetSheetListener.value = it
                }
            }
            launch {
                betRepo.observeComboBean().collect {
                    setNumberLimit(it.minAmount, it.maxAmount)
                    val balance = balanceRepo.getBalance()
                    if (it.inputMoney > balance) {
                        it.inputMoney = 0
                        setOverNumberListener(NumberOverEnum.OVER_REMAINING)
                    } else if (it.inputMoney > it.maxAmount) {
                        it.inputMoney = it.maxAmount
                    }
                    if (it.inputMoney > 0L) {
                        setEditNumber(it.inputMoney)
                    }
                }
            }
            launch {
                balanceRepo.observeBalance().collect {
                    _onBalanceListener.value = it
                    setRemainingNumber(it)
                }
            }
        }
    }

    fun sendBet() {
        val money = onEditNumber.value?.toMoney() ?: return
        return betRepo.sendBet(money)
    }

    fun removeBet() {
        betRepo.removeBet()
    }

    fun saveToCombo() {
        betRepo.saveToCombo()
    }

    fun saveReserveOdds(odds: Int) {
        betRepo.saveReserve(odds)
    }
}