package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.SparseArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.BOOM_1
import com.cn.game.sdk2.websocket.bean.BOOM_2
import com.cn.game.sdk2.websocket.bean.BOOM_3
import com.cn.game.sdk2.websocket.bean.BOOM_4
import com.cn.game.sdk2.websocket.bean.BOOM_5
import com.cn.game.sdk2.websocket.bean.BOOM_6
import com.cn.game.sdk2.websocket.bean.Betting
import com.xcjh.base_lib2.base.BaseViewModel

class LeopardVm : BaseViewModel() {

    val bettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = BOOM_1()
            it[2] = BOOM_2()
            it[3] = BOOM_3()
            it[4] = BOOM_4()
            it[5] = BOOM_5()
            it[6] = BOOM_6()
        }
    }


    @JvmOverloads
    fun multiplierStr(number: Int,format:String="x#.##"): String = CommonExt.multiplierStr(bettingArray[number],format)
}