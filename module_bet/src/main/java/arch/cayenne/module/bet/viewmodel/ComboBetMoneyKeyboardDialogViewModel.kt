package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.database.entity.InfoBean
import kotlinx.coroutines.launch

class ComboBetMoneyKeyboardDialogViewModel(
    private val balanceRepo: BalanceRepository
) : NumberCalculatorViewModel() {

    private val _onBalanceListener = MutableLiveData<InfoBean>()
    val onBalanceListener: LiveData<InfoBean> get() = _onBalanceListener


    init {
        viewModelScope.launch {

            launch {
                balanceRepo.observeBalance().collect {
                    _onBalanceListener.value = it
                }
            }
        }
    }
}