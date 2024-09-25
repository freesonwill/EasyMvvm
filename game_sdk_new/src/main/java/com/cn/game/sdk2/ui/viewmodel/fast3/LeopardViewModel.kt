package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getBoomBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting

class LeopardViewModel : ViewModel() {
    val leopardBettingArray by lazy {
        getBoomBets().toSpareArray()
    }

    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String = "×#.##"): String =
        CommonExt.multiplierStr(betting, format)
}