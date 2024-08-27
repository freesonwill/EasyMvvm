package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.utils.BettingAreaUtil.getDoubleBets
import com.cn.game.sdk2.utils.ext.CommonExt
import com.xcjh.base_lib2.base.BaseViewModel

class PairsDiceVm : BaseViewModel() {

    val bettingArray by lazy {
        getDoubleBets()
    }

    @JvmOverloads
    fun multiplierStr(number: Int,format:String="x#.##"): String = CommonExt.multiplierStr(bettingArray[number],format)
}