package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.data.NumberOverEnum
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.module.bet.repo.ReserveRepository
import arch.cayenne.module.bet.repo.SingleBetRepository
import kotlinx.coroutines.launch

class ReserveViewModel(private val repo: ReserveRepository, private val singleRepo: SingleBetRepository, private val balanceRepo: BalanceRepository) : NumberCalculatorViewModel() {

    private val _onReserveSheetListener = MutableLiveData<BetSelectionBean>()
    val onReserveSheetListener: LiveData<BetSelectionBean> get() =  _onReserveSheetListener

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
                repo.observeSelectionBean().collect {
                    _onReserveSheetListener.value = it
                }
            }
            launch {
                repo.observeComboBean().collect {
                    _onOddsListener.value = it.sumOdds
                    setNumberLimit(it.minAmount, it.maxAmount)
                    val oriData = onEditNumber.value
                    if (oriData == null) {
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
                    } else {
                        val curMoney = oriData.toMoney()
                        if (curMoney > 0L) {
                            setEditNumber(curMoney)
                        }
                    }
                }
            }
        }
    }

    fun removeReserve() {
        repo.removeReserve()
    }

    fun removeBet() {
        singleRepo.removeBet()
    }

    fun saveToCombo() {
        singleRepo.saveToCombo()
    }

    fun sendReserve() {
        val money = onEditNumber.value?.toMoney() ?: return
        return repo.sendReserve(money)
    }
}