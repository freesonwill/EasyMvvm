package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class SingleBetViewModel(private val betRepo: SingleBetRepository) : NumberCalculatorViewModel() {

    private val _onBetSheetListener = MutableLiveData<BetBean>()
    val onBetSheetListener: LiveData<BetBean> get() =  _onBetSheetListener

    private val _onBetWinMoney = MediatorLiveData<String>().apply {
        val odds = 1.0f
        addSource(_onBetSheetListener) { data ->
            data.odds *= odds
        }
        addSource(_onEditMoney) {
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
                val betMoney = money.toFloatOrNull() ?: 0f
                String.format("%.2f", betMoney * odds)
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

    }

    fun removeBet() {
        _onBetSheetListener.value?.let {
            betRepo.removeBet(it.gameId)
        }
    }

    fun saveToCombo() {
        _onBetSheetListener.value?.let {
            betRepo.saveToCombo(it.gameId)
        }
    }
}