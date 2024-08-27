package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.utils.BettingAreaUtil.getBoomBets
import com.cn.game.sdk2.utils.ext.CommonExt
import com.xcjh.base_lib2.base.BaseViewModel

class LeopardVm : BaseViewModel() {

    val bettingArray by lazy {
        getBoomBets()
    }


    @JvmOverloads
    fun multiplierStr(number: Int,format:String="x#.##"): String = CommonExt.multiplierStr(bettingArray[number],format)
}