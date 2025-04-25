package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.repo.BetResultRepository
import kotlinx.coroutines.launch

class BetResultViewModel(private val repo: BetResultRepository): BaseViewModel() {

    private val _onBetSheetListener = MutableLiveData<List<BetBean>>()
    val onBetSheetListener: LiveData<List<BetBean>> get() = _onBetSheetListener

    private val _onBetModeListener = MutableLiveData<Pair<BetTypeEnum, BetStatusEnum>>()
    val onBetModeListener: LiveData<Pair<BetTypeEnum, BetStatusEnum>> get() = _onBetModeListener

    private fun setBetMode(type: BetTypeEnum, status: BetStatusEnum) {
        _onBetModeListener.value = Pair(type, status)
    }

    fun setResultId(id: Long) {
        setBets(id)
    }

    private fun setBets(id: Long) {
        viewModelScope.launch {
            val bets = repo.getBets(id)
            if (bets.isNotEmpty()) {
                _onBetSheetListener.value = bets
            }
        }
    }
}