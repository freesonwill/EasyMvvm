package com.cn.game.sdk2.ui.fast3

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.data.EventConst
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.view.BetteView
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.ToastUtil
import com.cn.game.sdk2.utils.ext.CommonExt.isCanGoOn
import com.cn.game.sdk2.utils.ext.ViewExt.locationOnScreen
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.gameMassageManager
import com.xcjh.base_lib.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/21 18:14
 **/
abstract class BaseFast3Fragment<VM : BaseViewModel, VB : ViewDataBinding>(var fast3VM: Fast3ViewModel) :
    BaseGameFragment<VM, VB>() {
    protected var areaViewList: MutableList<GameAreaView> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        initAreaViewList()
        for (areaView in areaViewList) {
            setMoneyOKClickListener(areaView)
        }
    }

    abstract fun initAreaViewList()

    override fun createObserver() {
        super.createObserver()
        fast3VM.userLotteryResultLiveData.observe(viewLifecycleOwner) { resultList ->
            setLotteryResult(resultList, fast3VM.prizeAnimTime, fast3VM.prizeAnimCount)
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        FlowBus.with<List<GameAreaView>>(EventConst.UPDATE_ALL_AREA_VIEW)
            .post(fast3VM.viewModelScope, areaViewList)
    }

    private fun setLotteryResult(
        resultList: ArrayList<Betting>,
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
    private suspend fun playAlphaAnimTogether(dic: List<View>, duration: Long, count: Int) {
        delay(100)
        suspendCoroutine { continuation ->
            fast3VM.playAlphaAnimationLD.value = true
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
                fast3VM.playAlphaAnimationLD.value = false
            })
            animatorSet.start()
        }
    }

    private fun setMoneyOKClickListener(areaView: GameAreaView) {
        areaView.moneyView.setMoneyOKClickListener(object : MoneyOKView.OnMoneyOKClickListener {
            override fun onConfirm() {
                fast3VM.betOkClick.value = true;
            }

            override fun onDelete() {
                fast3VM.betDeleteClick.value = true;
            }
        })

        areaView.setOnLocationClickListener(object : GameAreaView.LocationClickListener {
            override fun onLocationClick(rawX: Float, rawY: Float) {
                addBetting(areaView, rawX, rawY)
            }
        })
    }

    private fun addBetting(areaView: GameAreaView, rawX: Float, rawY: Float) {
        //先判断余额是否够这次 并且扣取钱
        if (fast3VM.isClickOperation && PromptSoundPlay.handleClick()) {
            val bettingBean = BettingRecordBean(areaView.areaInfo!!, money = fast3VM.betMoney)
            gameMassageManager?.addBetting(bettingBean) { bettingState, result,areaLimit ->
                bettingState.isCanGoOn(areaLimit,areaView.areaInfo){
                    result?.let {
                        areaView.setShowMoney(result.money)
                        if (!areaView.moneyView.isAdd()) {
                            addMoneyOkView(areaView, rawX, rawY) {
                                emitMoneyAnim(result, areaView, isNewAdd = true)
                            }
                        } else {
                            emitMoneyAnim(result, areaView)
                        }
                    }
                }
            }
        }
    }


    abstract fun addMoneyOkView(
        areaView: GameAreaView,
        rawX: Float,
        rawY: Float,
        emitAnimCallBack: () -> Unit
    )

    protected fun handleViewTranslation(
        it: MoneyOKView,
        areaView: GameAreaView,
        rawX: Float,
        rawY: Float,
    ) {
        val betteView = it.findViewById<ImageView>(R.id.ivShowBg)
        val betteLocation = betteView.locationOnScreen
        val betteX = betteLocation[0]
        val betteY = betteLocation[1]

        val moneyLocation = it.locationOnScreen
        val moneyX = moneyLocation[0]

        val areaLocation = areaView.locationOnScreen
        val areaX = areaLocation[0]
        val areaY = areaLocation[1]

        //筹码最终偏移的位置
        val endX = rawX - betteView.measuredWidth / 2
        val endY = rawY - betteView.measuredHeight / 2
        //偏移距离
        val dx = endX - betteX
        val dy = endY - betteY

        //是否左右边界
        val isLeftStart = areaView.id == R.id.small_view
                || areaView.id == R.id.single_view
                || areaView.id == R.id.gavDiceOne
                || areaView.id == R.id.gavDiceSix
                || areaView.id == R.id.gavSumFour
                || areaView.id == R.id.gavSumNine
                || areaView.id == R.id.gavSumFourteen
                || areaView.id == R.id.gavPairsOne
                || areaView.id == R.id.gavPairsSix
                || areaView.id == R.id.gavLeopardOne
                || areaView.id == R.id.gavLeopardSix
        val isRightEnd = areaView.id == R.id.big_view
                || areaView.id == R.id.double_view
                || areaView.id == R.id.gavDiceTwo
                || areaView.id == R.id.gavDiceFive
                || areaView.id == R.id.gavSumEight
                || areaView.id == R.id.gavSumThirteen
                || areaView.id == R.id.gavPairsTwo
                || areaView.id == R.id.gavPairsFive
                || areaView.id == R.id.gavLeopardTwo
                || areaView.id == R.id.gavLeopardFive

        val limitLeft = if (areaView.id == R.id.double_view) {
            val leopardLocation = IntArray(2)
            (mDatabind as FragDxdsBinding).leopardView.getLocationOnScreen(leopardLocation)
            val leopardX = leopardLocation[0]
            leopardX + (mDatabind as FragDxdsBinding).leopardView.measuredWidth
        } else {
            areaX
        }
        val limitRight = if (areaView.id == R.id.single_view) {
            val leopardLocation = IntArray(2)
            (mDatabind as FragDxdsBinding).leopardView.getLocationOnScreen(leopardLocation)
            val leopardX = leopardLocation[0]
            leopardX
        } else {
            areaX + areaView.measuredWidth
        }
        val limitBottom = when (areaView.id) {
            R.id.small_view, R.id.big_view -> {
                val smallLocation = IntArray(2)
                (mDatabind as FragDxdsBinding).txtSmallMoney.getLocationOnScreen(smallLocation)
                smallLocation[1]
            }

            R.id.single_view, R.id.double_view -> {
                val smallLocation = IntArray(2)
                (mDatabind as FragDxdsBinding).txtSingleMoney.getLocationOnScreen(smallLocation)
                smallLocation[1]
            }

            else -> {
                areaY + areaView.measuredHeight
            }
        }
        val limitTop = areaY

        it.translationX = when {
            isLeftStart -> {
                when {
                    dx >= 0 && endX + betteView.measuredWidth <= limitRight -> dx
                    endX + betteView.measuredWidth > limitRight -> dx + limitRight - endX - betteView.measuredWidth
                    else -> 0f
                }
            }

            isRightEnd -> {
                when {
                    endX >= limitLeft && moneyX + dx + it.measuredWidth <= limitRight -> dx
                    endX < limitLeft -> dx + limitLeft - endX
                    moneyX + dx + it.measuredWidth > limitRight -> (limitRight - moneyX - it.measuredWidth).toFloat()
                    else -> 0f
                }
            }

            else -> {
                when {
                    endX >= limitLeft && endX + betteView.measuredWidth <= limitRight -> dx
                    endX < limitLeft -> dx + limitLeft - endX
                    endX + betteView.measuredWidth > limitRight -> dx + limitRight - endX - betteView.measuredWidth
                    else -> 0f
                }
            }
        }

        it.translationY = when {
            endY + betteView.measuredHeight > limitBottom -> (limitBottom - betteY - betteView.measuredHeight).toFloat()
            endY < limitTop -> (limitTop - betteY).toFloat()
            else -> dy
        }

        areaView.betteView.translationX = it.translationX
        areaView.betteView.translationY = it.translationY
    }

    private fun emitMoneyAnim(
        recordBean: BettingRecordBean,
        areaView: GameAreaView,
        isNewAdd: Boolean = false
    ) {
        val moneyOKView = areaView.moneyView
        val betteView = areaView.betteView

        recordBean.viewXYTemporary[0] = moneyOKView.translationX
        recordBean.viewXYTemporary[1] = moneyOKView.translationY
        Log.e(
            Fast3MainFragment.TAG,
            "坐标信息--->${moneyOKView.translationX} ${moneyOKView.translationY}"
        )
        val location = moneyOKView.ivShowBg.locationOnScreen
        val rax = location[0].toFloat()
        val ray = location[1].toFloat()
        if (isNewAdd) {
            moneyOKView.ivShowBg.isInvisible = true
            moneyOKView.parentView = moneyOKView.parent as ViewGroup
            betteView.parentView = betteView.parent as ViewGroup
            betteView.isVisible = false
        }
        fast3VM.emitMoneyAnim(rax, ray, areaView = areaView, endCallBack = {
            betteView.isVisible = true
        })
    }
}