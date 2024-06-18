package com.cn.game.sdk2.ui.fast3

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.base.BaseGameFragment
import com.cn.game.sdk2.data.enums.NOTES_ENUM
import com.cn.game.sdk2.databinding.FragDxdsBinding
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.utils.MyGameManager
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.DXDSVm
import com.cn.game.sdk2.utils.tool.measureView
import me.jessyan.autosize.utils.AutoSizeUtils.dp2px
import kotlin.math.abs


/**
 * 默认
 */
class DXDSFragment(var fast3VM: Fast3ViewModel) : BaseGameFragment<DXDSVm, FragDxdsBinding>() {
    private var animators: MutableList<ObjectAnimator> = mutableListOf()

    private var areaViewList: MutableList<GameAreaView> = mutableListOf();

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.apply {
            bigView.areaCode = NOTES_ENUM.QTDefaultBig.num
            smallView.areaCode = NOTES_ENUM.QTDefaultSmall.num
            singleView.areaCode = NOTES_ENUM.QTDefaultSingle.num
            doubleView.areaCode = NOTES_ENUM.QTDefaultDouble.num
            leopardView.areaCode = NOTES_ENUM.QTDefaultTriple.num
            areaViewList.add(bigView)
            areaViewList.add(smallView)
            areaViewList.add(singleView)
            areaViewList.add(doubleView)
            areaViewList.add(leopardView)
        }

        for (areaView in areaViewList) {
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
                    if (MyGameManager.isClickOperation && PromptSoundPlay.handleClick()) {
                        fast3VM.anchorMoneyView?.get()?.let {
                            if (it !== areaView.moneyView) {
                                it.hiddenTop()
                            }
                        }
                        //动画位置
                        areaView.moneyView.let {
                            if (it.isAdd()) {
                                val location = IntArray(2)
                                it.getLocationOnScreen(location)
                                val xOnScreen = location[0]
                                val yOnScreen = location[1]
                                //通过显示的控件得到相对于屏幕的位置
                                var rax = xOnScreen
                                var ray = yOnScreen + dp2px(context, 47f)

                                fast3VM.emitMoneyAnim(
                                    rax.toFloat(),
                                    ray.toFloat(),
                                    areaView = areaView
                                )
                            } else {
                                it.viewTreeObserver.addOnGlobalLayoutListener(object :
                                    OnGlobalLayoutListener {
                                    override fun onGlobalLayout() {
                                        // 确保只监听一次
                                        it.viewTreeObserver.removeOnGlobalLayoutListener(
                                            this
                                        )

                                        // 获取视图在屏幕上的绝对位置
                                        val location = IntArray(2)
                                        it.getLocationOnScreen(location)
                                        val xOnScreen = location[0]
                                        val yOnScreen = location[1]


                                        //通过显示的控件得到相对于屏幕的位置
                                        val rax = xOnScreen
                                        val ray = yOnScreen + dp2px(context, 47f)

                                        fast3VM.emitMoneyAnim(rax.toFloat(),
                                            ray.toFloat(),
                                            areaView = areaView,
                                            endCallBack = {
                                                //动画结束后再显示View
                                                it.isVisible = true
                                            })

                                        //显示点击在Fragment的位置用于动画结束后显示
                                        val location1 = IntArray(2)
                                        it.getLocationInWindow(location1)
                                        it.viewXYTemporary[0] = it.left
                                        it.viewXYTemporary[1] = it.top
                                    }
                                })
                                val params = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                areaView.addView(it, params)
                                // 将新按钮设置为居底部，方便向上偏移
                                // 豹子暂时居中，不做偏移
                                params.gravity =
                                    if (areaView.id == R.id.leopard_view) Gravity.CENTER else Gravity.BOTTOM
                                if (it.measuredWidth <= 0) {
                                    it.measureView()
                                }

                                //默认偏移控件中心点
                                val dx = x - it.measuredWidth / 2
                                val dy = y - it.measuredHeight / 2


                                //处理水平偏移
                                val areaWidth = areaView.width
                                when (areaView.id) {
                                    R.id.small_view, R.id.big_view -> {
                                        it.translationX = when {
                                            //不超出左右边界
                                            dx > 0 && dx + it.measuredWidth <= areaWidth -> dx
                                            dx + it.measuredWidth > areaWidth -> (areaWidth - it.measuredWidth).toFloat()
                                            else -> 0f
                                        }
                                    }

                                    R.id.single_view -> {
                                        //单 处理有边界
                                        val leopardLocation = IntArray(2)
                                        mDatabind.leopardView.getLocationOnScreen(leopardLocation)
                                        val leopardX = leopardLocation[0]
                                        it.translationX = when {
                                            //不超出左右边界
                                            dx > 0 && dx + it.measuredWidth <= leopardX -> dx
                                            dx + it.measuredWidth > leopardX -> (leopardX - it.measuredWidth).toFloat()
                                            else -> 0f
                                        }
                                    }


                                    R.id.double_view -> {
                                        //双 处理左边界
                                        val leopardLocation = IntArray(2)
                                        mDatabind.leopardView.getLocationOnScreen(leopardLocation)
                                        val leopardX = leopardLocation[0]
                                        val leftX =
                                            leopardX + mDatabind.leopardView.width - areaWidth
                                        it.translationX = when {
                                            //不超出左右边界
                                            dx > leftX && dx + it.measuredWidth <= areaWidth -> dx
                                            dx + it.measuredWidth > areaWidth -> (areaWidth - it.measuredWidth).toFloat()
                                            else -> leftX.toFloat()
                                        }
                                    }
                                }

                                //处理垂直偏移
                                val areaLocation = IntArray(2)
                                areaView.getLocationOnScreen(areaLocation)
                                val areaY = areaLocation[1]
                                when (areaView.id) {
                                    // 小 大
                                    R.id.small_view, R.id.big_view -> {
                                        val smallLocation = IntArray(2)
                                        mDatabind.txtSmallMoney.getLocationOnScreen(smallLocation)
                                        val textY = smallLocation[1]
                                        it.translationY = when {
                                            rawY + it.measuredHeight / 2 > textY -> (textY - areaY - areaView.height).toFloat()
                                            else -> dy
                                        }
                                    }

                                    //单双
                                    R.id.single_view, R.id.double_view -> {
                                        val singLocation = IntArray(2)
                                        mDatabind.txtSingleMoney.getLocationOnScreen(singLocation)
                                        val textY = singLocation[1]
                                        it.translationY = when {
                                            rawY + it.measuredHeight / 2 > textY -> (textY - areaY - areaView.height).toFloat()
                                            else -> dy
                                        }
                                    }
                                }

                                it.isGone = true
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animators.forEach { it.cancel() }
        animators.clear()
    }
}
