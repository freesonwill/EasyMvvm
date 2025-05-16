package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import plugin.koin.KoinViewModel

@KoinViewModel
class BetSlipPageViewModel:BaseViewModel() {
    var matchId: Long = 0
    var sportId: Int = 0
}