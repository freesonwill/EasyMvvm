package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.module.bet.repo.FloatingButtonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

    suspend fun getSingleBetById() = viewModelScope.async {
        repo.getSingleBetId().apply {
            repo.saveToSingleBet(this@apply)
        }
    }.await()
}