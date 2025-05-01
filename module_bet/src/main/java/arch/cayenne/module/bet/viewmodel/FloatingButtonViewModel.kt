package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.bet.repo.FloatingButtonRepository
import kotlinx.coroutines.launch
import plugin.koin.KoinViewModel

@KoinViewModel
class FloatingButtonViewModel(private val repo: FloatingButtonRepository) : BaseViewModel() {

    private val _onBettingCount = MutableLiveData(0)
    val onBettingCount: LiveData<Int> = _onBettingCount

    init {
        viewModelScope.launch {
            repo.observeComboBetCount().collect {
                _onBettingCount.value = it
            }
        }
    }

    fun saveToSingle() {
        repo.saveToSingleBet()
    }
}