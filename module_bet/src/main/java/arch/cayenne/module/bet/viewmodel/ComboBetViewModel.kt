package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.repo.ComboBetRepository
import kotlinx.coroutines.launch

class ComboBetViewModel(
    private val repo: ComboBetRepository,
    private val balanceRepo: BalanceRepository
) : BaseViewModel() {

    private val _onComboMultiBetBeanListener = MutableLiveData<List<ComboMultiBetBean>>()
    val onComboMultiBetBeanListener: LiveData<List<ComboMultiBetBean>> get() = _onComboMultiBetBeanListener

    private val _onBetListListener = MediatorLiveData<List<BetSelectionBean>>().apply {
        addSource(_onComboMultiBetBeanListener) { bean ->
            val failBet = bean.isEmpty() || bean.map { it.minAmount == 0L && it.maxAmount == 0L }.any { it }
            if (failBet) {
                value = value?.map { it.copy(isActive = false) }
            }
        }
    }
    val onBetListListener: LiveData<List<BetSelectionBean>> get() = _onBetListListener

    private val _onBalanceListener = MutableLiveData<InfoBean>()
    val onBalanceListener: LiveData<InfoBean> get() = _onBalanceListener

    private val _onCanBetListener = MediatorLiveData(false).apply {
        val updateCanBet = {
            val betList = _onBetListListener.value
            val comboData = _onComboMultiBetBeanListener.value

            value = if (betList != null && comboData != null) {
                betList.size > 1 &&
                        betList.all { it.isActive && it.isParlay } &&
                        comboData.any { it.inputMoney > 0L }
            } else {
                false
            }
        }

        addSource(_onBetListListener) { updateCanBet() }
        addSource(_onComboMultiBetBeanListener) { updateCanBet() }
    }
    val onCanBetListener: LiveData<Boolean> get() = _onCanBetListener

    private val _onForceUpdateListener = MediatorLiveData(false).apply {
        var isFirstInit = false
        val checkBothLoaded = {
            if (isFirstInit) {
                val comboMultiData = _onComboMultiBetBeanListener.value
                val betListData = _onBetListListener.value
                if (comboMultiData != null && betListData != null) {
                    if (comboMultiData.size < 3 && _onMultiLayoutExpendListener.value == true) {
                        setExpandMultiLayout(false)
                        value = true
                    } else if (betListData.size == 2) {
                        value = true
                    }
                }

            } else {
                if (_onBetListListener.value != null && _onComboMultiBetBeanListener.value != null) {
                    isFirstInit = true
                    value = true
                }
            }
        }

        addSource(_onBetListListener) { checkBothLoaded() }
        addSource(_onComboMultiBetBeanListener) { checkBothLoaded() }
    }
    val onForceUpdateListener: LiveData<Boolean> get() = _onForceUpdateListener

    private val _onMultiLayoutExpendListener = MutableLiveData<Boolean>(false)
    val onMultiLayoutExpendListener: LiveData<Boolean> get() = _onMultiLayoutExpendListener

    val remainingBalance: Long
        get() = onBalanceListener.value?.let { infoBean ->
            onComboMultiBetBeanListener.value?.sumOf { it.amount }?.let { betAmount ->
                infoBean.balance - betAmount
            } ?: infoBean.balance
        } ?: 0


    val moneySymbol: String
        get() = CurrencySymbols.getSymbol(_onBalanceListener.value?.currency ?: "")

    init {
        viewModelScope.launch {
            launch {
                repo.observeComboBet().collect {
                    if (it.isEmpty()) {
                        _onBetListListener.value = emptyList()
                    } else if (it.size == 1) {
                        _onBetListListener.value = it
                    } else {
                        setBetList(it)
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
                            val oldBean = oriData.find { it.serialValue == newBean.serialValue }
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

    fun updateMultiBetMoney(serialValue: Int, money: Long) {
        _onComboMultiBetBeanListener.value?.let {
            val updatedList = it.map { rate ->
                if (rate.serialValue == serialValue) {
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
        onComboMultiBetBeanListener.value?.filter { it.inputMoney != 0L }?.let {
            repo.saveInputMoney(it)
        }
    }

    private fun setBetList(betList: List<BetSelectionBean>) {
        _onBetListListener.value = betList
    }

    fun toggleMultiLayoutExpend() {
        _onMultiLayoutExpendListener.value = _onMultiLayoutExpendListener.value?.not() ?: true
    }

    private fun setExpandMultiLayout(expand: Boolean) {
        val currentValue = _onMultiLayoutExpendListener.value ?: false
        if (currentValue == expand) return // No change needed
        _onMultiLayoutExpendListener.value = expand
    }
}