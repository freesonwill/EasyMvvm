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

    fun setBetSheet(id: Int?) {
        viewModelScope.launch {
            if (id == null) {
                launch {
                    repo.observeComboBet().collect {
                        setBetData(it)
                    }
                }
            } else {
                launch {
                    repo.observeBetById(id).collect {
                        if (it != null) {
                            setBetData(listOf(it))
                        }
                    }
                }
            }
        }
    }

    private fun setBetData(data: List<BetBean>) {
        _onBetSheetListener.value = data
        if (data.size == 1) {
            val bean = data.first()
            setBetMode(bean.betType, bean.status)
        } else {
            val hasBetting = data.any { it.status == BetStatusEnum.BETTING }
            val hasFail = data.any { it.status == BetStatusEnum.FAIL }
            val allComplete = data.all { it.status == BetStatusEnum.COMPLETE }

            val status = when {
                hasBetting -> BetStatusEnum.BETTING
                hasFail -> BetStatusEnum.FAIL
                allComplete -> BetStatusEnum.COMPLETE
                else -> BetStatusEnum.BETTING
            }
            setBetMode(BetTypeEnum.COMBO, status)
        }
    }
}