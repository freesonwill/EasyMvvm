package com.cn.game.sdk2.ui.fast3

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.data.EventConst
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.xcjh.base_lib.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/21 18:14
 **/
abstract class BaseFast3Fragment<VM : BaseViewModel, VB : ViewDataBinding> (var fast3VM: Fast3ViewModel):
    BaseGameFragment<VM, VB>() {
    protected var areaViewList: MutableList<GameAreaView> = mutableListOf()

    override fun createObserver() {
        super.createObserver()
        FlowBus.with<List<GameAreaView>>(EventConst.UPDATE_ALL_AREA_VIEW).post(fast3VM.viewModelScope,areaViewList)
    }
    protected fun setLotteryResult(
        resultList: ArrayList<Betting>,
        areaViewList: MutableList<GameAreaView>,
        duration: Long,
        count: Int
    ) {
        lifecycleScope.launch {
            val views = mutableListOf<View>()
            for (areaView in areaViewList) {
                if (resultList.contains(areaView.areaInfo)) {
                    views.add(areaView.flickerView)
                }
            }
            if (views.isNotEmpty()) {
                playAlphaAnimTogether(views, duration, count)
            }
        }
    }

    /**
     * 播放透明度动画
     */
    protected suspend fun playAlphaAnimTogether(dic: List<View>, duration: Long, count: Int) {
        suspendCoroutine { continuation ->
            val animatorSet = AnimatorSet()
            val animators = dic.map { maskView ->
                val animator = ObjectAnimator.ofFloat(maskView, "alpha", 1f, 0f, 1f).apply {
                    this.duration = duration // 设置动画持续时间
                    this.repeatCount = count // 设置无限循环
                    this.repeatMode = ObjectAnimator.REVERSE // 设置反向循环以实现渐隐渐显效果
                    this.addListener(onStart = {
                        maskView.isVisible = true
                        Log.d(TAG, "maskView-->${maskView} visible true")
                    }, onEnd = {
                        it.cancel()
                        maskView.isVisible = false
                        Log.d(TAG, "maskView-->${maskView} visible false")
                    })
                }
                animator
            }
            animatorSet.playTogether(animators)
            animatorSet.addListener(onEnd = {
                dic.forEach {
                    it.isVisible = false
                    Log.d(TAG, "maskView-->${it} visible false")
                }
                continuation.resume("")
            })
            animatorSet.start()
        }
    }

    protected fun setMoneyOKClickListener(areaView: GameAreaView, fast3VM: Fast3ViewModel) {
        areaView.moneyView.setMoneyOKClickListener(object : MoneyOKView.OnMoneyOKClickListener {
            override fun onConfirm() {
                fast3VM.betOkClick.value = true;
            }

            override fun onDelete() {
                fast3VM.betDeleteClick.value = true;
            }
        })

        areaView.moneyView.setMoneyOKClickListener(object : MoneyOKView.OnMoneyOKClickListener {
            override fun onConfirm() {
                fast3VM.betOkClick.value = true;
            }

            override fun onDelete() {
                fast3VM.betDeleteClick.value = true;
            }
        })
        areaView.setOnLocationClickListener(object : GameAreaView.LocationClickListener {
            override fun onLocationClick(x: Float, y: Float, rawX: Float, rawY: Float) {
                //处理点击事件
                //先判断余额是否够这次 并且扣取钱
                //if( homeXPopupDialog.isCanBetting()&&MyGameManager.isClickOperation&&PromptSoundPlay.handleClick()){
                //todo:整个流程转移至FastMainFragment
                if (fast3VM.isClickOperation && PromptSoundPlay.handleClick()) {
                    GameSocketManager.getInstance()?.getGameService()?.apply {
                        addBetting(
                            BettingRecordBean(
                                areaView.areaInfo!!,
                                money = fast3VM.betMoney
                            )
                        ) { isMoneyEnough, result ->
                            if (isMoneyEnough) {
                                if (result != null) {
//                                    fast3VM.currentBettingRecordBean = Pair(result,areaView.moneyView)
                                    if (!areaView.moneyView.isAdd()) {
                                        addMoneyOkView(areaView, areaView, x, y, rawY)
                                    }
                                    emitMoneyAnim(areaView, areaView.moneyView, fast3VM)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    /**
     * 添加moneyView 计算偏移
     */
    private fun addMoneyOkView(
        areaView: GameAreaView,
        leopardView: View,
        x: Float,
        y: Float,
        rawY: Float
    ) {

    }

    private fun emitMoneyAnim(
        areaView: GameAreaView,
        moneyOKView: MoneyOKView,
        fast3VM: Fast3ViewModel
    ) {
        val location = moneyOKView.locationOnScreen
        val rax = location[0].toFloat()
        val ray = location[1].toFloat() + moneyOKView.measuredHeight / 2
        fast3VM.emitMoneyAnim(rax, ray, areaView = areaView, endCallBack = {
            moneyOKView.isVisible = true
//            显示点击在Fragment的位置用于动画结束后显示
//            val location1 = IntArray(2)
//            moneyOKView.getLocationInWindow(location1)
//            moneyOKView.viewXYTemporary[0] = moneyOKView.left
//            moneyOKView.viewXYTemporary[1] = moneyOKView.top
        })
    }

}