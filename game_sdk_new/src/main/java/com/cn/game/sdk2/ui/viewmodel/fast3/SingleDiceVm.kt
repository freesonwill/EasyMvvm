package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.utils.BettingAreaUtil.getSingleBets
import com.cn.game.sdk2.utils.ext.CommonExt
import com.xcjh.base_lib2.base.BaseViewModel

class SingleDiceVm : BaseViewModel() {

    val bettingArray by lazy {
        getSingleBets()
    }


    @JvmOverloads
    fun multiplierStr(number: Int,format:String="#.##"): String = CommonExt.multiplierStr(bettingArray[number],format)
}