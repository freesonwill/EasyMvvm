package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.SparseArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.DOUBLE_1
import com.cn.game.sdk2.websocket.bean.DOUBLE_2
import com.cn.game.sdk2.websocket.bean.DOUBLE_3
import com.cn.game.sdk2.websocket.bean.DOUBLE_4
import com.cn.game.sdk2.websocket.bean.DOUBLE_5
import com.cn.game.sdk2.websocket.bean.DOUBLE_6
import com.xcjh.base_lib2.base.BaseViewModel

class PairsDiceVm : BaseViewModel() {

    val bettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = DOUBLE_1()
            it[2] = DOUBLE_2()
            it[3] = DOUBLE_3()
            it[4] = DOUBLE_4()
            it[5] = DOUBLE_5()
            it[6] = DOUBLE_6()
        }
    }

    @JvmOverloads
    fun multiplierStr(number: Int,format:String="x#.##"): String = CommonExt.multiplierStr(bettingArray[number],format)
}