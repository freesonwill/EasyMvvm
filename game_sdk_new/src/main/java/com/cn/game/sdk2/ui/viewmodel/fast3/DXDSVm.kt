package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.utils.BettingAreaUtil.getDefaultBets
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.base.BaseViewModel

class DXDSVm : BaseViewModel() {
    val bettingArray by lazy {
        getDefaultBets()
    }
    @JvmOverloads
    fun multiplierStr(number: Int,format:String="x#.##"): String = CommonExt.multiplierStr(bettingArray[number],format)
    val syncAreaBetInfoLD = gameAboutModel.syncAreaBetInfo

}