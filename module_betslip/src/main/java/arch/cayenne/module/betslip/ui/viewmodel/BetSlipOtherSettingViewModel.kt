package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.module.betslip.data.repo.BetSlipOtherSettingRepository
import kotlinx.coroutines.launch

class BetSlipOtherSettingViewModel(
    private val repo: BetSlipOtherSettingRepository,
    private val balanceRepo: BalanceRepository
) : BaseViewModel() {

    private val _onBalanceListener = MutableLiveData<InfoBean>()
    val onBalanceListener: LiveData<InfoBean> get() = _onBalanceListener

    val moneySymbol: String
        get() = CurrencySymbols.getSymbol(_onBalanceListener.value?.currency?:"")

    val isBetSlipDetail: Boolean
        get() = repo.isBetSlipDetail()

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