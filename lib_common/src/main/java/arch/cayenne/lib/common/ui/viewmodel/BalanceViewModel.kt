package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.data.repo.BalanceRepository
import kotlinx.coroutines.launch

class BalanceViewModel(
    private val balanceRepository: BalanceRepository
) : BaseViewModel() {

    private val _onBalanceChange = MutableLiveData<BaseCurrencyData.CurrencyContentData?>()
    val onBalanceChange: LiveData<BaseCurrencyData.CurrencyContentData?> = _onBalanceChange

    var userCurrency: Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>>? =
        null

    private val _onUserCurrencyChange =
        MutableLiveData<Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>>>()
    val onUserCurrencyChange: LiveData<Pair<List<BaseCurrencyData.CurrencyContentData>, List<BaseCurrencyData.CurrencyContentData>>> =
        _onUserCurrencyChange

    init {
        viewModelScope.launch {
            balanceRepository.observeUserCurrency().collect {
                _onBalanceChange.value = it
            }
        }
    }

    fun getUserCurrency() {
        viewModelScope.launch {
            userCurrency = balanceRepository.getUserCurrency()
            _onUserCurrencyChange.value = userCurrency
        }
    }

    fun search(keyword: String) {
        if (keyword.isEmpty()) {
            _onUserCurrencyChange.value = userCurrency
            return
        }
        viewModelScope.launch {
            _onUserCurrencyChange.value = balanceRepository.search(keyword)
        }
    }

    fun setDefaultCurrency(ccy: String) {
        balanceRepository.setDefaultCurrency(ccy)
    }

    fun setFiatCurrency(ccy: String) {
        balanceRepository.setFiatCurrency(ccy)
    }
}