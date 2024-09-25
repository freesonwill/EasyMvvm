package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.base.BaseGameViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getSumBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray

class SumTotalViewModel  : BaseGameViewModel() {
    val sumTotalBettingArray by lazy {
        getSumBets().toSpareArray(4)
    }
}