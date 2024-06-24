package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.SparseArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.SINGLE_1
import com.cn.game.sdk2.websocket.bean.SINGLE_2
import com.cn.game.sdk2.websocket.bean.SINGLE_3
import com.cn.game.sdk2.websocket.bean.SINGLE_4
import com.cn.game.sdk2.websocket.bean.SINGLE_5
import com.cn.game.sdk2.websocket.bean.SINGLE_6
import com.xcjh.base_lib.base.BaseViewModel

class SingleDiceVm : BaseViewModel() {

    val bettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = SINGLE_1()
            it[2] = SINGLE_2()
            it[3] = SINGLE_3()
            it[4] = SINGLE_4()
            it[5] = SINGLE_5()
            it[6] = SINGLE_6()
        }
    }


    fun multiplierStr(number: Int): String = CommonExt.multiplierStr(bettingArray[number])
}