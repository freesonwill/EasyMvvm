package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.repo.BalanceRepository
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class SingleBetViewModel(private val betRepo: SingleBetRepository, private val balanceRepo: BalanceRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MutableLiveData<BetBean>()
    val onBetSheetListener: LiveData<BetBean> get() =  _onBetSheetListener

    private val _onBalanceListener = MutableLiveData<Long>()
    val onBalanceListener: LiveData<Long> get() = _onBalanceListener

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 1
        addSource(_onBetSheetListener) { data ->
            odds *= data.selectionLiteBean.odds.toOdds()
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
                betRepo.observeSingleBet().collect {
                    if (it != null) {
                        _onBetSheetListener.value = it
                        setNumberLimit(it.minAmount, it.maxAmount)
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
        val id = _onBetSheetListener.value?.matchId ?: return
        val money = onEditNumber.value?.toMoney() ?: return
        betRepo.sendBet(id, money)
    }

    fun removeBet() {
        _onBetSheetListener.value?.let {
            betRepo.removeBet(it.matchId)
        }
    }

    fun saveToCombo() {
        _onBetSheetListener.value?.let {
            betRepo.saveToCombo(it.matchId)
        }
    }
}