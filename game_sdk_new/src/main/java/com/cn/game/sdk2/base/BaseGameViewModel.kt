package com.cn.game.sdk2.base

import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting

open class BaseGameViewModel : ViewModel() {
    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String = "×#.##"): String =
        CommonExt.multiplierStr(betting, format)

    @JvmOverloads
    fun multiplierSingStr(betting: Betting, format: String = "#.##"): String =
        CommonExt.multiplierStr(betting, format)
}