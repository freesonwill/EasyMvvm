package arch.cayenne.module.order.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class HomeOrderViewModel : BaseViewModel() {
    val betModelFlow = MutableStateFlow<BetMode?>(null)

}

enum class BetMode {
    BET_RECORD, //投注记录，可查看游戏和体育的注单
    BET_SLIP, //注单,只可查看体育的注单
}