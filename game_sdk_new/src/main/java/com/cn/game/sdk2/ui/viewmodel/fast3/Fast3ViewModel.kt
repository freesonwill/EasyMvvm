package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.LiveData
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.MutablePair
import java.lang.ref.WeakReference

class Fast3ViewModel:BaseViewModel() {
    var betOkClick:UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick:UnPeekLiveData<Boolean> = UnPeekLiveData()

    var anchorMoneyView:WeakReference<MoneyOKView> ?=null

    //<areaCode,<money,View>>
    var savedMoneyMap:MutableMap<Int,MutablePair<Int,WeakReference<MoneyOKView>>> = mutableMapOf()
    var tempMoneyMap:MutableMap<Int,MutablePair<Int,WeakReference<MoneyOKView>>> = mutableMapOf()
    var moneyAnimCallback: MoneyAnimCallback?= null

    fun emitMoneyAnim(x: Float, y: Float, isCentered: Boolean = false, speed: Long = 300, areaView: GameAreaView,endCallBack:(()->Unit)?=null){
        moneyAnimCallback?.apply {
            startAnim(x, y, isCentered, speed, areaView,endCallBack)
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
        fun startAnim(x: Float, y: Float,isCentered: Boolean = false, speed: Long = 300, areaView: GameAreaView,endCallBack: (() -> Unit)?)
    }
}