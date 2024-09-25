package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.base.BaseGameViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getDefaultBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray

class DXDSViewModel : BaseGameViewModel() {
    val dXDSBettingArray by lazy {
        getDefaultBets().toSpareArray()
    }
}