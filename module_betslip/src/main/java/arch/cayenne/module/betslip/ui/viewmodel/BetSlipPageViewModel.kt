package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import plugin.koin.KoinViewModel
import org.koin.java.KoinJavaComponent.inject


@KoinViewModel
class BetSlipPageViewModel : BaseViewModel() {
    var matchId: Long = 0
    var sportId: Int = 0
    private val userManager: UserDataManager by inject(UserDataManager::class.java)

    //设置是否注单详情
    fun setBetSlipDetail() {
        userManager.setKeyValue(UserDataKey.KEY_BETSLIP_DETAIL, false)
    }

}