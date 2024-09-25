package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.base.BaseGameViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getBoomBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray

class LeopardViewModel : BaseGameViewModel() {
    val leopardBettingArray by lazy {
        getBoomBets().toSpareArray()
    }
}