package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import kotlinx.coroutines.launch

class ComboBetMoneyKeyboardDialogViewModel(
    private val balanceRepo: BalanceRepository
) : NumberCalculatorViewModel() {

    private val _onCurrencyListener = MutableLiveData<String>()
    val onCurrencyListener: LiveData<String> get() = _onCurrencyListener


    init {
        viewModelScope.launch {

            launch {
                balanceRepo.observeCurrency().collect {
                    _onCurrencyListener.value = it
                }
            }
        }
    }
}