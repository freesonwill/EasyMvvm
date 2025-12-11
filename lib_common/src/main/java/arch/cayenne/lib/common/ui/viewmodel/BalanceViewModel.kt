package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.repo.BalanceRepository
import kotlinx.coroutines.launch

class BalanceViewModel(
    private val balanceRepository: BalanceRepository
): BaseViewModel() {

    private val _onBalanceChange = MutableLiveData<Long>()
    val onBalanceChange: LiveData<Long> = _onBalanceChange

    init {
        viewModelScope.launch {
            balanceRepository.observeBalance().collect {
                _onBalanceChange.value = it
            }
        }

    }

}