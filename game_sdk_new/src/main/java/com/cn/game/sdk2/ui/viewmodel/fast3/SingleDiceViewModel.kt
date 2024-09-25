package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.base.BaseGameViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getSingleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray

class SingleDiceViewModel : BaseGameViewModel() {
    val singleDiceBettingArray by lazy {
        getSingleBets().toSpareArray()
    }
}