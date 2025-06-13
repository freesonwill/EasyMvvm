package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.betslip.data.repo.BetSlipOtherSettingRepository

class BetSlipOtherSettingViewModel(private val repo: BetSlipOtherSettingRepository): BaseViewModel() {

    val isBetSlipDetail: Boolean
        get() = repo.isBetSlipDetail()

}