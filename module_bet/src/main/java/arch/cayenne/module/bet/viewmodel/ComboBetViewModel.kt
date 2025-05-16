package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.module.bet.repo.ComboBetRepository
import kotlinx.coroutines.launch

class ComboBetViewModel(private val repo: ComboBetRepository, private val balanceRepo: BalanceRepository) : BaseViewModel() {

    private val _onBetListListener = MutableLiveData<List<BetSelectionBean>>()
    val onBetListListener: LiveData<List<BetSelectionBean>> get() = _onBetListListener

    private val _onComboMultiBetBeanListener = MutableLiveData<List<ComboMultiBetBean>>()
    val onComboMultiBetBeanListener: LiveData<List<ComboMultiBetBean>> get() = _onComboMultiBetBeanListener

    private val _onBalanceListener = MutableLiveData<Long>()
    val onBalanceListener: LiveData<Long> get() = _onBalanceListener

    val remainingBalance: Long
        get() = onBalanceListener.value?.let { balance ->
            onComboMultiBetBeanListener.value?.sumOf { it.amount }?.let { betAmount ->
                balance - betAmount
            } ?: balance
        } ?: 0

    init {
        viewModelScope.launch {
            launch {
                repo.observeComboBet().collect {
                    _onBetListListener.value = it
                    if (it.size <= 1) {
                        if (it.isNotEmpty()) {
                            repo.saveToSingleBet()
                        }
                    }
                }
            }
            launch {
                repo.observeComboMultiBet().collect { beans ->
                    val oriData = _onComboMultiBetBeanListener.value
                    if (oriData.isNullOrEmpty()) {
                        val balance = balanceRepo.getBalance()
                        val sumMoney = beans.sumOf { it.inputMoney }
                        if (sumMoney > balance) {
                            beans.forEach { it.inputMoney = 0L }
                        }
                    } else {
                        beans.forEach { newBean ->
                            val oldBean = oriData.find { it.combo == newBean.combo }
                            if (oldBean != null) {
                                newBean.inputMoney = oldBean.inputMoney
                            }
                        }
                    }
                    setMultiBetBean(beans)
                }
            }
            launch {
                balanceRepo.observeBalance().collect {
                    _onBalanceListener.value = it
                }
            }
        }
    }

    fun removeSelection(selectionId: Long) {
        repo.removeSelection(selectionId)
    }

    fun removeAll() {
        repo.removeAll()
    }

    fun updateMultiBetMoney(combo: Int, money: Long) {
        _onComboMultiBetBeanListener.value?.let {
            val updatedList = it.map { rate ->
                if (rate.combo == combo) {
                    rate.copy(inputMoney = money)
                } else {
                    rate
                }
            }
            setMultiBetBean(updatedList)
        }
    }

    fun sendBet() {
        _onComboMultiBetBeanListener.value?.filter { it.inputMoney != 0L }?.let {
            if (it.isNotEmpty()) {
                repo.sendBet(it)
            }
        }
    }

    private fun setMultiBetBean(data: List<ComboMultiBetBean>) {
        _onComboMultiBetBeanListener.value = data
    }

    fun saveInputMoney() {
        onComboMultiBetBeanListener.value?.let {
            repo.saveInputMoney(it)
        }
    }
}