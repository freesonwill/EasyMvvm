package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.repo.BalanceRepository
import arch.cayenne.module.bet.repo.ReserveRepository
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class ReserveViewModel(private val repo: ReserveRepository, private val betRepo: SingleBetRepository, private val balanceRepo: BalanceRepository) : NumberCalculatorViewModel() {

    private val _onReserveSheetListener = MutableLiveData<BetBean>()
    val onReserveSheetListener: LiveData<BetBean> get() =  _onReserveSheetListener

    private val _onOddsListener = MutableLiveData<Int>()
    val onOddsListener: LiveData<Int> get() = _onOddsListener

    private val _onBalanceListener = MutableLiveData<Long>()
    val onBalanceListener: LiveData<Long> get() = _onBalanceListener

    private val odds: Int get() = _onOddsListener.value ?: 1

    private val _onReserveWinMoney = MediatorLiveData<String>().apply {
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
    val onReserveWinMoney: LiveData<String> get() = _onReserveWinMoney

    init {
        viewModelScope.launch {
            launch {
                balanceRepo.observeBalance().collect {
                    _onBalanceListener.value = it
                }
            }
            launch {
                repo.observeReserveBet().collect {
                    if (it != null) {
                        _onReserveSheetListener.value = it
                        setNumberLimit(it.minAmount, it.maxAmount)
                    }
                }
            }
        }
    }

    fun setOdds(odds: Int) {
        _onOddsListener.value = odds
    }

    fun removeReserve() {
        _onReserveSheetListener.value?.let {
            repo.removeReserve(it.matchId)
        }

    }

    fun removeBet() {
        _onReserveSheetListener.value?.let {
            betRepo.removeBet(it.matchId)
        }
    }

    fun saveToCombo() {
        _onReserveSheetListener.value?.let {
            betRepo.saveToCombo(it.matchId)
        }
    }

    fun sendReserve() {
        val id = onReserveSheetListener.value?.matchId ?: return
        val odds = onOddsListener.value ?: return
        val money = onEditNumber.value?.toMoney() ?: return
        repo.sendReserve(id, odds, money)
    }
}