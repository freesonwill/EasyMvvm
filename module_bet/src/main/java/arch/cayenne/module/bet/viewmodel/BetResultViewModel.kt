package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.repo.BetResultRepository
import kotlinx.coroutines.launch

class BetResultViewModel(private val repo: BetResultRepository) : BaseViewModel() {

    private val _onBetSheetListener = MutableLiveData<List<BetBean>>()
    val onBetSheetListener: LiveData<List<BetBean>> get() = _onBetSheetListener

    private val _onDetailListener = MutableLiveData<List<BetResultDetailBean>>()
    val onDetailListener: LiveData<List<BetResultDetailBean>> get() = _onDetailListener


    private val _onBetModeListener = MutableLiveData<Pair<BetTypeEnum, BetResultStatusEnum>>()
    val onBetModeListener: LiveData<Pair<BetTypeEnum, BetResultStatusEnum>> get() = _onBetModeListener

    private fun setDetail(data: List<BetResultDetailBean>) {
        _onDetailListener.value = data
        val status = if (data.any { it.status == BetResultStatusEnum.CONFIRMING }) {
            BetResultStatusEnum.CONFIRMING
        } else if (data.all { it.status == BetResultStatusEnum.SUCCESS_BET }) {
            BetResultStatusEnum.SUCCESS_BET
        } else {
            BetResultStatusEnum.REJECT
        }
        setBetMode(status)
    }

    fun setResultId(id: Long) {
        setBets(id)
        observeResultDetail(id)
    }

    private fun setBets(id: Long) {
        viewModelScope.launch {
            val bets = repo.getBets(id)
            if (bets.isNotEmpty()) {
                _onBetSheetListener.value = bets
                setBetMode(BetResultStatusEnum.CONFIRMING)
            }
        }
    }

    private fun observeResultDetail(id: Long) {
        viewModelScope.launch {
            repo.observeResultDetail(id).collect {
                if (it.isNotEmpty()) {
                    setDetail(it)
                }
            }
        }
    }

    private fun setBetMode(status: BetResultStatusEnum) {
        val data = _onBetSheetListener.value
        val type = if (data == null) {
            BetTypeEnum.SINGLE
        } else if (data.size == 1) {
            data.first().betType
        } else {
            BetTypeEnum.COMBO
        }
        _onBetModeListener.value = Pair(type, status)

    }

    fun clearBetBean() {
        repo.clearBetBean()
    }
}