package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getSumBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting

class SumTotalViewModel  : ViewModel() {
    val sumTotalBettingArray by lazy {
        getSumBets().toSpareArray(4)
    }

    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String = "×#.##"): String =
        CommonExt.multiplierStr(betting, format)
}