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
import arch.cayenne.module.bet.repo.BetResultRepository
import kotlinx.coroutines.launch

class BetResultViewModel(private val repo: BetResultRepository) : BaseViewModel() {

    private val _onBetSheetListener = MutableLiveData<List<BetSelectionBean>>()
    val onBetSheetListener: LiveData<List<BetSelectionBean>> get() = _onBetSheetListener

    private val _onDetailListener = MutableLiveData<List<BetDetailBean>>()
    val onDetailListener: LiveData<List<BetDetailBean>> get() = _onDetailListener

    private val _betType = MutableLiveData<BetTypeEnum>()
    val betType: LiveData<BetTypeEnum> get() = _betType

    private val _onBetModeListener = MediatorLiveData<Pair<BetTypeEnum, BetResultStatusEnum>>().apply {
        fun set(status: BetResultStatusEnum, type: BetTypeEnum) {
            value = Pair(type, status)
        }

        fun getStatus(data: List<BetDetailBean>): BetResultStatusEnum {
            val status = if (data.any { it.status == BetResultStatusEnum.CONFIRMING || it.status == null }) {
                BetResultStatusEnum.CONFIRMING
            } else if (data.all { it.status == BetResultStatusEnum.SUCCESS_BET }) {
                BetResultStatusEnum.SUCCESS_BET
            } else {
                BetResultStatusEnum.REJECT
            }
            return status
        }
        addSource(_onDetailListener) {
            val type = _betType.value ?: return@addSource
            val status = getStatus(it)
            set(status, type)
        }
        addSource(_betType) {
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
                    _betType.value = type
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
                _currencyListener.value = repo.getCurrency()
            }
        }
    }

    suspend fun continueBet() = repo.continueBet()
    fun sendDone() {
        repo.sendDone()
    }
}