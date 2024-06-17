package com.cn.game.sdk2.viewmodel.fast3

import android.view.View
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.MutablePair
import java.lang.ref.WeakReference
import java.math.BigInteger

class Fast3ViewModel:BaseViewModel() {
    public var betOkClick:UnPeekLiveData<Boolean> = UnPeekLiveData()
    public var betDeleteClick:UnPeekLiveData<Boolean> = UnPeekLiveData()

    public lateinit var betUnit:BigInteger
    var anchorMoneyView:WeakReference<MoneyOKView> ?=null

    //<areaCode,<money,View>>
    public var savedMoneyMap:MutableMap<Int,MutablePair<Int,WeakReference<MoneyOKView>>> = mutableMapOf()
    public var tempMoneyMap:MutableMap<Int,MutablePair<Int,WeakReference<MoneyOKView>>> = mutableMapOf()
    public var moneyAnimCallback: MoneyAnimCallback ?= null

    public fun emitMoneyAnim(x: Float, y: Float, isCentered: Boolean = false, speed: Long = 300, areaView: GameAreaView){
        moneyAnimCallback?.apply {
            startAnim(x, y, isCentered, speed, areaView)
        }
    }

    fun updateAnchorView(anchor: MoneyOKView){
        anchorMoneyView?.get()?.apply {
            hiddenTop()
        }
        anchor.showTop()
        anchorMoneyView = WeakReference(anchor)
    }

    interface MoneyAnimCallback{
        fun startAnim(x: Float, y: Float,isCentered: Boolean = false, speed: Long = 300, areaView: GameAreaView)
    }
}