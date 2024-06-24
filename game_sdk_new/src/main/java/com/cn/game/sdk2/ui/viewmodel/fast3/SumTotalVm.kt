package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.SparseArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.SUM_10
import com.cn.game.sdk2.websocket.bean.SUM_11
import com.cn.game.sdk2.websocket.bean.SUM_12
import com.cn.game.sdk2.websocket.bean.SUM_13
import com.cn.game.sdk2.websocket.bean.SUM_14
import com.cn.game.sdk2.websocket.bean.SUM_15
import com.cn.game.sdk2.websocket.bean.SUM_16
import com.cn.game.sdk2.websocket.bean.SUM_17
import com.cn.game.sdk2.websocket.bean.SUM_4
import com.cn.game.sdk2.websocket.bean.SUM_5
import com.cn.game.sdk2.websocket.bean.SUM_6
import com.cn.game.sdk2.websocket.bean.SUM_7
import com.cn.game.sdk2.websocket.bean.SUM_8
import com.cn.game.sdk2.websocket.bean.SUM_9
import com.xcjh.base_lib.base.BaseViewModel

class SumTotalVm  : BaseViewModel() {
    val bettingArray by lazy {
        SparseArray<Betting>().also {
            it[4] = SUM_4()
            it[5] = SUM_5()
            it[6] = SUM_6()
            it[7] = SUM_7()
            it[8] = SUM_8()
            it[9] = SUM_9()
            it[10] = SUM_10()
            it[11] = SUM_11()
            it[12] = SUM_12()
            it[13] = SUM_13()
            it[14] = SUM_14()
            it[15] = SUM_15()
            it[16] = SUM_16()
            it[17] = SUM_17()
        }
    }


    fun multiplierStr(number: Int): String = CommonExt.multiplierStr(bettingArray[number])
}