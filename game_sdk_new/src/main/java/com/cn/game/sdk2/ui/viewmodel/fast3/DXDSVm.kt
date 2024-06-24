package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.SparseArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.BOOM_ALL
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.DEFAULT_BIG
import com.cn.game.sdk2.websocket.bean.DEFAULT_DOUBLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SINGLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SMALL
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib.base.BaseViewModel

class DXDSVm : BaseViewModel() {
    val bettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = DEFAULT_BIG()
            it[2] = DEFAULT_SMALL()
            it[3] = DEFAULT_SINGLE()
            it[4] = DEFAULT_DOUBLE()
            it[5] = BOOM_ALL()
        }
    }
    fun multiplierStr(number: Int): String = CommonExt.multiplierStr(bettingArray[number])
    val syncAreaBetInfoLD = gameAboutModel.syncAreaBetInfo

}