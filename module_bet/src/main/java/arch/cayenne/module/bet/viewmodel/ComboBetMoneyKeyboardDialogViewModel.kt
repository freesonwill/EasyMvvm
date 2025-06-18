package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel

class ComboBetMoneyKeyboardDialogViewModel: NumberCalculatorViewModel() {

    private val _moneySymbolListener = MutableLiveData(CurrencySymbols.CNY.symbol)
    val moneySymbolListener: LiveData<String> get() = _moneySymbolListener
    val moneySymbol: String
        get() = _moneySymbolListener.value ?: CurrencySymbols.CNY.symbol
}