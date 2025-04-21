package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.repo.ReserveRepository
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class ReserveViewModel(private val repo: ReserveRepository, private val betRepo: SingleBetRepository) : NumberCalculatorViewModel() {

    private val _onReserveSheetListener = MutableLiveData<BetBean>()
    val onReserveSheetListener: LiveData<BetBean> get() =  _onReserveSheetListener

    private val _onBalanceListener = MutableLiveData(123456L)
    val onBalanceListener: LiveData<Long> get() = _onBalanceListener

    private val _onReserveWinMoney = MediatorLiveData<String>().apply {
        var odds = 1
        addSource(_onReserveSheetListener) { data ->
            odds *= data.reverseOdds ?: 1
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
    val onReserveWinMoney: LiveData<String> get() = _onReserveWinMoney

    fun setReserveBet(id: Long) {
        viewModelScope.launch {
            repo.getReverseById(id)?.let {
                _onReserveSheetListener.value = it
                setNumberLimit(it.minAmount, it.maxAmount)
            }
        }
    }

    fun removeReserve(id: Long) {
        repo.removeReserve(id)
    }

    fun removeBet() {
        _onReserveSheetListener.value?.let {
            betRepo.removeBet(it.matchId)
        }
    }

    fun saveToCombo() {
        _onReserveSheetListener.value?.let {
            repo.updateReserveOdds(it.matchId, null)
            betRepo.saveToCombo(it.matchId)
        }
    }

    fun sendReserve() {
        val id = _onReserveSheetListener.value?.matchId ?: return
        val money = onEditNumber.value?.toMoney() ?: return
        repo.sendReserve(id, money)
    }
}