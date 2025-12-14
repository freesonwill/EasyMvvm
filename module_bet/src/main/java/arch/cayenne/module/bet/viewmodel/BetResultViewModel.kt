package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.repo.BetResultRepository
import arch.cayenne.module.bet.repo.ComboBetRepository
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment
import kotlinx.coroutines.launch

class BetResultViewModel(private val repo: BetResultRepository, private val betRepo: ComboBetRepository) : BaseViewModel() {

    private val _onBetSheetListener = MutableLiveData<List<BetSelectionBean>>()
    val onBetSheetListener: LiveData<List<BetSelectionBean>> get() = _onBetSheetListener

    private val _onDetailListener = MutableLiveData<List<BetDetailBean>>()
    val onDetailListener: LiveData<List<BetDetailBean>> get() = _onDetailListener

    private val _onBetType = MutableLiveData<BetTypeEnum>()
    val onBetType: LiveData<BetTypeEnum> get() = _onBetType
    private var comboMultiBetBeans: List<ComboMultiBetBean>? = null
    private val _onBetModeListener =
        MediatorLiveData<Pair<BetTypeEnum, BetResultStatusEnum>>().apply {
            fun set(status: BetResultStatusEnum, type: BetTypeEnum) {
                value = Pair(type, status)
            }

            fun getStatus(data: List<BetDetailBean>): BetResultStatusEnum {
                val status =
                    if (data.any { it.status == BetResultStatusEnum.CONFIRMING || it.status == null }) {
                        BetResultStatusEnum.CONFIRMING
                    } else if (data.all { it.status == BetResultStatusEnum.SUCCESS_BET }) {
                        BetResultStatusEnum.SUCCESS_BET
                    } else if (data.isEmpty()) {
                        BetResultStatusEnum.CREATE
                    } else {
                        BetResultStatusEnum.REJECT
                    }
                return status
            }
            addSource(_onDetailListener) {
                val type = _onBetType.value ?: return@addSource
                val status = getStatus(it)
                set(status, type)
            }
            addSource(_onBetType) {
                val detail = _onDetailListener.value ?: return@addSource
                val status = getStatus(detail)
                set(status, it)
            }
        }
    val onBetModeListener: LiveData<Pair<BetTypeEnum, BetResultStatusEnum>> get() = _onBetModeListener

    private val _currencyListener = MutableLiveData<String>()


    val moneySymbol: String
        get() = CurrencySymbols.getSymbol(_currencyListener.value ?: "")

    init {
        viewModelScope.launch {
            launch {
                repo.observeBetType().collect { type ->
                    _onBetType.value = type
                }
            }
            launch {
                repo.observeSelections().collect { selections ->
                    _onBetSheetListener.value = selections
                }
            }
            launch {
                repo.observeDetail().collect { detail ->
                    _onDetailListener.value = detail
                }
            }
            launch {
                repo.observeCurrency().collect {
                    _currencyListener.value = it
                }
            }
        }
    }

    suspend fun continueBet() = repo.continueBet()

    fun sendDone() {
        repo.sendDone()
    }

    fun setComboMultiBetBeans(list:List<ComboMultiBetBean>){
        this.comboMultiBetBeans = list
    }

    fun toCombinationDetailParameter(serialValue: Int): ComboDetailFragment.Parameter {
        val data = comboMultiBetBeans?.find { it.serialValue == serialValue }?: error("can not find serialValue:$serialValue in $comboMultiBetBeans")
        return betRepo.toCombinationDetailParameter(
            data,
            onBetSheetListener.value,
            moneySymbol
        )
    }
}