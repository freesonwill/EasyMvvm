package arch.cayenne.module.bet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
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


    private val _onBetModeListener = MutableLiveData<Pair<BetTypeEnum, BetResultStatusEnum>>()
    val onBetModeListener: LiveData<Pair<BetTypeEnum, BetResultStatusEnum>> get() = _onBetModeListener

    var type: BetTypeEnum = BetTypeEnum.SINGLE
        private set

    init {
        viewModelScope.launch {
            repo.getLastOrderBet()?.let {
                type = it.betType
                Log.d("abcd", "+++ $it")
                val selection = repo.getSelection(it.betId)

                _onBetSheetListener.value = selection

                launch {
                    repo.observeDetail(it.betId).collect { detail ->
                        _onDetailListener.value = detail
                        setModeByDetail(detail)
                    }
                }
            }
        }
    }

    private fun setModeByDetail(data: List<BetDetailBean>) {
        val status = if (data.any { it.status == BetResultStatusEnum.CONFIRMING }) {
            BetResultStatusEnum.CONFIRMING
        } else if (data.all { it.status == BetResultStatusEnum.SUCCESS_BET }) {
            BetResultStatusEnum.SUCCESS_BET
        } else {
            BetResultStatusEnum.REJECT
        }
        setBetMode(status)
    }

    private fun setBetMode(status: BetResultStatusEnum) {
        _onBetModeListener.value = Pair(type, status)
    }

    suspend fun continueBet() = repo.continueBet()
}