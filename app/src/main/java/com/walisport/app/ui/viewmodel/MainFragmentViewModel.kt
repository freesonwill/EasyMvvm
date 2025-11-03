package com.walisport.app.ui.viewmodel

import android.graphics.drawable.Drawable
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.order.ui.viewmodel.BetMode
import kotlinx.coroutines.flow.MutableStateFlow
import plugin.koin.KoinViewModel

/**
 * @date: 2025/11/1 13:56
 * @description:
 */

@KoinViewModel
class MainFragmentViewModel:BaseViewModel() {
    val betSlotFlow = MutableStateFlow(BetSlot.BET_RECORD)
    val selectedIndexFlow = MutableStateFlow(0)

}
enum class BetSlot(val mode: Int, val icon: Drawable, val title:String){
    BET_RECORD(1, arch.cayenne.module.home.R.drawable.ic_betslip.getDrawable(),arch.cayenne.lib.common.R.string.drawer_bet_record.getString()),
    BET_SLIP(0, arch.cayenne.module.home.R.drawable.ic_betslip.getDrawable(), arch.cayenne.lib.res.R.string.bet_title.getString());
    fun toBetMode(): BetMode {
        return BetMode.entries.toTypedArray()[this.ordinal]
    }
}
