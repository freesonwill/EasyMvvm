package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.common.utils.ext.CollectionExt.combinations
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.isGreaterThanValue
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.OddsChangeEnum
import arch.cayenne.module.bet.repo.ComboBetRepository
import arch.cayenne.module.bet.ui.fragment.CombinationFragment
import kotlinx.coroutines.launch

class ComboBetViewModel(
    private val repo: ComboBetRepository,
    private val balanceRepo: BalanceRepository
) : BaseViewModel() {

    private val _onComboMultiBetBeanListener = MutableLiveData<List<ComboMultiBetBean>>()
    val onComboMultiBetBeanListener: LiveData<List<ComboMultiBetBean>> get() = _onComboMultiBetBeanListener
    val firstComboMultiBetBeanLD: LiveData<ComboMultiBetBean>  = _onComboMultiBetBeanListener.map { list ->
        list.first()
    }
    val remainingComboMultiBetBeansLD: LiveData<List<ComboMultiBetBean>>  = _onComboMultiBetBeanListener.map { list ->
        list.drop(1)
    }

    private val _onBetListListener = MutableLiveData<List<BetSelectionBean>>()
    val onBetListListener: LiveData<List<BetSelectionBean>> get() = _onBetListListener

    private val _onBalanceListener = MutableLiveData<InfoBean?>()
    val onBalanceListener: LiveData<InfoBean?> get() = _onBalanceListener

    val balance: Long
        get() = _onBalanceListener.value?.balance ?: 0L

    private val _onForceUpdateListener = MediatorLiveData(false).apply {
        var updateBox = Pair(false, false)
        var hasInit = false
        var lastBetSize = 0
        var lastComboSize = 0
        val checkBothLoaded = {
            if (hasInit) {
                val comboMultiData = _onComboMultiBetBeanListener.value
                val betListData = _onBetListListener.value
                if (comboMultiData != null && betListData != null) {
                    if (updateBox.first && updateBox.second) {
                        value = true
                        updateBox = Pair(false, false)
                    }
                }
            } else {
                if (_onBetListListener.value != null && _onComboMultiBetBeanListener.value != null) {
                    hasInit = true
                    value = true
                }
            }
        }

        addSource(_onBetListListener) {
            if (it.size > lastBetSize) {
                value = true
                lastBetSize = it.size
                return@addSource
            } else if (lastBetSize > it.size) {
                if (lastBetSize == 3 && it.size == 2) {
                    value = true
                    lastBetSize = it.size
                    return@addSource
                }
            }
            checkBothLoaded()
            if (lastBetSize != it.size) {
                updateBox = Pair(true, updateBox.second)
            }
            lastBetSize = it.size
        }
        addSource(_onComboMultiBetBeanListener) {
            checkBothLoaded()
            if (lastComboSize != it.size) {
                updateBox = Pair(updateBox.first, true)
            }
            lastComboSize = it.size
        }
    }
    val onForceUpdateListener: LiveData<Boolean> get() = _onForceUpdateListener

    private val _onMultiLayoutExpendListener = MutableLiveData<Boolean>(false)
    val onMultiLayoutExpendListener: LiveData<Boolean> get() = _onMultiLayoutExpendListener

    val moneySymbol: String
        get() = CurrencySymbols.getSymbol(_onBalanceListener.value?.currency ?: "")

    private val _networkConnectedEvent = MutableLiveData<Event<DataState>>()
    val networkConnectedEvent: LiveData<Event<DataState>> get() = _networkConnectedEvent

    private val _oddsChangeListener = MutableLiveData<OddsChangeEnum>()
    val oddsChangeListener: LiveData<OddsChangeEnum> get() = _oddsChangeListener

    init {
        viewModelScope.launch {
            launch {
                repo.observeComboBet().collect {
                    _onBetListListener.value = it
                }
            }
            launch {
                repo.observeComboMultiBet().collect { beans ->
                    val lastList = _onComboMultiBetBeanListener.value
                    //"aaaa---observeComboMultiBet--nowList:$beans, lastList:$lastList,${this@ComboBetViewModel}".logd(TAG)

                    // 如果舊資料是 null，代表第一次載入，直接設值
                    if (lastList == null) {
                        _onComboMultiBetBeanListener.value = beans
                    } else {
                        //旧资料的钱拷贝到新资料
                        val updatedList = beans.mapIndexed { index, newItem ->
                            val oldItem = lastList.getOrNull(index)
                            val updatedInputMoney = oldItem?.inputMoney?: newItem.inputMoney
                            newItem.copy(inputMoney = updatedInputMoney)
                        }
                        _onComboMultiBetBeanListener.value = updatedList
                    }
                }
            }
            launch {
                balanceRepo.observeInfo().collect {
                    _onBalanceListener.value = it
                }
            }
            launch {
                repo.observeOddsChange().collect {
                    _oddsChangeListener.value = it
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
        //"updateMultiBetMoney---$serialValue,money:$money".logd(TAG)
        _onComboMultiBetBeanListener.value?.let {
            val updatedList = it.map { rate ->
                if (rate.serialValue == serialValue) {
                    rate.copy(inputMoney = money)
                } else {
                    rate
                }
            }
            repo.setMoney(serialValue, money)
            _onComboMultiBetBeanListener.value = updatedList
        }
    }

    fun getSumBetAmount(): Long {
        return _onComboMultiBetBeanListener.value?.sumOf { it.inputMoney } ?: 0L
    }

    fun sendBet(): Boolean {
        if (!checkNetwork()) {
            return false
        }
        val oddsChangeEnum = _oddsChangeListener.value ?: return false
        return _onComboMultiBetBeanListener.value?.filter { it.inputMoney != 0L }?.let {
            if (it.isNotEmpty()) {
                repo.sendBet(it, oddsChangeEnum)
                true
            } else {
                false
            }
        } ?: run {
            false
        }
    }

    fun toggleMultiLayoutExpend() {
        _onMultiLayoutExpendListener.value = _onMultiLayoutExpendListener.value?.not() ?: true
    }

    fun setExpandMultiLayout(expand: Boolean) {
        val currentValue = _onMultiLayoutExpendListener.value ?: false
        if (currentValue == expand) return // No change needed
        _onMultiLayoutExpendListener.value = expand
    }

    fun clearBetMoney() {
        val data = _onComboMultiBetBeanListener.value ?: return
        _onComboMultiBetBeanListener.value = data.map {
            if (it.inputMoney > 0) {
                it.copy(inputMoney = 0L)
            } else {
                it
            }
        }
    }

    suspend fun getBetSize(): Int = repo.getBetSize()

    private fun checkNetwork(): Boolean {
        if (!repo.isConnected) {
            _networkConnectedEvent.value = Event(DataState.NetworkUnavailable)
            return false
        }
        return true
    }

    fun checkOddsPass(): Boolean {
        val oddsChange = _oddsChangeListener.value ?: return false
        if (oddsChange == OddsChangeEnum.ANY) {
            return true
        }
        val anyChange = _onBetListListener.value?.any { it.initialOdds != it.odds } ?: return false
        if (oddsChange == OddsChangeEnum.NO_CHANGE && anyChange) {
            return false
        }
        val anyWorse = _onBetListListener.value?.any { it.initialOdds > it.odds } ?: return false
        if (oddsChange == OddsChangeEnum.BETTER && anyWorse) {
            return false
        }
        return true
    }

    /**
     * 检查限额
     * @return
     */
    fun checkAmountLimit():String? {
        val data = _onComboMultiBetBeanListener.value ?: return null
        val balance = this.balance
        var totalInputMoney = 0L
        for((i,d) in data.withIndex()){
            totalInputMoney += d.inputMoney
            if(!d.hasSetMoney()) continue
            val curAmount = d.inputMoney.getMoney()
            val maxMoney = d.maxAmount
            val minNumber = d.minAmount
            if (curAmount.isGreaterThanValue(maxMoney.getMoney())) {
                return d.title() + arch.cayenne.lib.common.R.string.toast_over_max.getString()
            }
            val amount = curAmount.toMoney()
            if (minNumber > amount) {
                return d.title() + R.string.hint_less_min_amount.getString()
            } else if (amount > balance) {
                return d.title() + arch.cayenne.lib.common.R.string.toast_over_remaining.getString()
            }
        }
        if(totalInputMoney == 0L) return R.string.hint_input_money.getString()
        return null
    }

    /**
     * 拆分串关，比如3串4拆成2串1，3串1
     * @param combK
     * @param combV
     * @return
     */
    fun splitComboIntoSingles(bean:ComboMultiBetBean): List<CombinationFragment.ParameterItems> {
        val data = this.onBetListListener.value ?: return emptyList()
        // 1 注 = 固定只有一个 K
        val kList = if (bean.comboV == 1) {
            listOf(bean.comboK)
        } else {
            ((if(bean.isSuperCombo) 1 else 2)..bean.comboK).toList()
        }
        return kList.map { k ->
            val title = R.string.title_combo_bet_detail.getString(
                if(k==1) arch.cayenne.lib.res.R.string.title_single_bet.getString()
                else R.string.title_combo_bet_odds.getString(k,1)
            )
            val moneySymbol = this.moneySymbol

            val listItems = data.combinations(k).map { l ->
                val odds = l.fold(1) { acc, c -> acc * c.odds }.let {
                    repo.getScaleOdds(it, (k - 1) * 2)
                }

                CombinationFragment.ParameterItems2(
                    combo = l.joinToString("·") { "${data.indexOf(it) + 1}" },
                    money = bean.inputMoney.takeIf { it != 0L }?.let { "$moneySymbol${it.getMoney()}" },
                    winMoney = bean.inputMoney.takeIf { it != 0L }?.let { "$moneySymbol${bean.inputMoney.getMoney(odds)}" },
                    odds = "@${odds.getOdds()}"
                )
            }
            CombinationFragment.ParameterItems(title, listItems)
        }
    }
}