package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.utils.ext.IntExt.getOdds
import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class SingleBetViewModel(private val betRepo: SingleBetRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MutableLiveData<BetBean>()
    val onBetSheetListener: LiveData<BetBean> get() =  _onBetSheetListener

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        var odds = 1
        addSource(_onBetSheetListener) { data ->
            odds *= data.selection.odds
        }
        addSource(_onEdidNumber) {
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
               money.toValue().getOdds(odds)
            }
        }
    }
    val onBetWinMoney: LiveData<String> get() = _onBetWinMoney

    init {
        viewModelScope.launch {
            betRepo.observeSingleBet().collect {
                if (it != null) {
                    _onBetSheetListener.value = it
                }
            }
        }
    }

    fun sendBet() {
        val id = _onBetSheetListener.value?.matchId ?: return
        val money = _onEdidNumber.value?.toValue() ?: return
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