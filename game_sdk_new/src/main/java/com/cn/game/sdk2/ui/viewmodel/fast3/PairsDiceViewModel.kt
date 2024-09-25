package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.base.BaseGameViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getDoubleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray

class PairsDiceViewModel : BaseGameViewModel() {
    val pairsDiceBettingArray by lazy {
        getDoubleBets().toSpareArray()
    }
}