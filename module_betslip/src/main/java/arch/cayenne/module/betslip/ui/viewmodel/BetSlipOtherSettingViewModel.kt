package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.module.betslip.data.repo.BetSlipOtherSettingRepository

class BetSlipOtherSettingViewModel(private val repo: BetSlipOtherSettingRepository): BaseViewModel() {

    private val _onMoneySymbolChangeListener = MutableLiveData(CurrencySymbols.CNY)
    val onMoneySymbolChangeListener: LiveData<String> get() = _onMoneySymbolChangeListener
    val moneySymbol: String
        get() = _onMoneySymbolChangeListener.value ?: CurrencySymbols.CNY

    val isBetSlipDetail: Boolean
        get() = repo.isBetSlipDetail()

}