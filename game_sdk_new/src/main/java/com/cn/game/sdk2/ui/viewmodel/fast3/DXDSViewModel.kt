package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.utils.BettingAreaUtil.getDefaultBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting

class DXDSViewModel : ViewModel() {
    val dXDSBettingArray by lazy {
        getDefaultBets().toSpareArray()
    }

    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String = "×#.##"): String =
        CommonExt.multiplierStr(betting, format)
}