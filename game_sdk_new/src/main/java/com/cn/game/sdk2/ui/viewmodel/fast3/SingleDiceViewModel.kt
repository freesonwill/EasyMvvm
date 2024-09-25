package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getSingleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting

class SingleDiceViewModel : ViewModel() {
    val singleDiceBettingArray by lazy {
        getSingleBets().toSpareArray()
    }

    @JvmOverloads
    fun multiplierSingStr(betting: Betting, format: String = "#.##"): String =
        CommonExt.multiplierStr(betting, format)
}