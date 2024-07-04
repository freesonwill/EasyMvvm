package com.cn.game.sdk2.ui.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.ViewBettingOkBinding
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.xcjh.base_lib.utils.StringFormatUtil
import com.xcjh.base_lib.utils.view.clickNoRepeat
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 选择钱以后点击确定
 */
@SuppressLint("ClickableViewAccessibility")
class MoneyOKView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    //总和没有添加到gameAreaView中，保存下被添加的父Layout用于续压
    var parentView: ViewGroup? = null
    var pageIndex: Int = 0

    /**
     * 是否显示取消或者确定
     */
//    var llShowTop: Group

    /**
     * 取消
     */
    var ivOff: ImageView

    /**
     * 确定
     */
    var ivOk: ImageView

    /**
     * 显示的钱
     */
    var ivShowMoney: AppCompatTextView

    /**
     * 显示的背景钱
     */
    var ivShowBg: AppCompatImageView

    var binding: ViewBettingOkBinding? = null


    // 声明一个变量来保存回调接口
    private var onMoneyOKClickListener: OnMoneyOKClickListener? = null

    fun setMoneyOKClickListener(listener: OnMoneyOKClickListener) {
        onMoneyOKClickListener = listener
    }

    /**
     * 点击事件
     */
    interface OnMoneyOKClickListener {
        /**
         * 关闭
         */
        fun onDelete()

        /**
         * 确定
         */
        fun onConfirm()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_betting_ok, this).apply {
            binding = ViewBettingOkBinding.bind(this)
//            llShowTop = binding!!.groupOkTopView
            ivShowMoney = binding!!.ivShowMoney
            ivShowBg = binding!!.ivShowBg
            ivOff = binding!!.ivOff
            ivOk = binding!!.ivOk
        }

        binding?.apply {
            offLayout.setOnTouchListener { v, event ->
                setTouchEvent(event, ivOff) {
                    PromptSoundPlay.btnPlayMedia(context)
                    onMoneyOKClickListener?.onDelete()
                }
                true
            }

            offLeftLayout.setOnTouchListener { v, event ->
                setTouchEvent(event, ivOff) {
                    PromptSoundPlay.btnPlayMedia(context)
                    onMoneyOKClickListener?.onDelete()
                }
                true
            }


            okLayout.setOnTouchListener { v, event ->
                setTouchEvent(event, ivOk) {
                    PromptSoundPlay.btnPlayMedia(context)
                    onMoneyOKClickListener?.onConfirm()
                }
                true
            }

            okRightLayout.setOnTouchListener { v, event ->
                setTouchEvent(event, ivOk) {
                    PromptSoundPlay.btnPlayMedia(context)
                    onMoneyOKClickListener?.onConfirm()
                }
                true
            }
        }
    }

    private fun startAnim(targetView: View, isBig: Boolean) {
        if (isBig) {
            AnimatorSet().apply {
                playTogether(
                    listOf(
                        ObjectAnimator.ofFloat(targetView, "scaleX", 0.8f, 1f).apply {
                            this.duration = 100
                        },
                        ObjectAnimator.ofFloat(targetView, "scaleY", 0.8f, 1f).apply {
                            this.duration = 100
                        }
                    )
                )
                start()
            }

        } else {
            AnimatorSet().apply {
                playTogether(
                    listOf(
                        ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 0.8f).apply {
                            this.duration = 100
                        },
                        ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 0.8f).apply {
                            this.duration = 100
                        }
                    )
                )
                start()
            }
        }
    }

    private var currentAnim: ScaleAnimation? = null
    private var currentView: View? = null

    private fun setTouchEvent(event: MotionEvent, targetView: View, clickAction: () -> Unit) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 按下时缩小
                startAnim(targetView, false)
                currentView = targetView
            }

            MotionEvent.ACTION_UP -> {
                // 抬起或取消时放大
                startAnim(targetView, true)
                currentView = targetView

                // 如果是抬起事件，触发点击事件
                if (event.action == MotionEvent.ACTION_UP) {
                    clickAction.invoke()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                resetAnim()
            }
        }
    }

    fun resetAnim() {
        currentView?.let {
            if (it.scaleX < 1f) {
                startAnim(it, true)
            }
        }
//        if(currentView.scaleX<1f){
//            currentView?.st
//        }
//        if (currentAnim == smallAnim) {
//            currentView?.startAnimation(bigAnim)
//        }
    }

    /**
     * 修改显示的钱
     */
    fun setShowMoney(money: Int) {
        ivShowMoney.text = showMoneyFormat(money)
    }

    /**
     * 隐藏头部的缺点和删除
     */
    fun hiddenTop() {
        ivOk.isInvisible = true
        ivOff.isInvisible = true
        binding?.apply {
            okLayout.isInvisible = true
            offLayout.isInvisible = true
            okRightLayout.isInvisible = true
            offLeftLayout.isInvisible = true
        }
//        binding!!.groupOkTopView.isInvisible = true
//        llShowTop.visibility = View.INVISIBLE
    }

    /**
     * 显示头部的确定和删除
     */
    fun showTop() {
        ivOk.isVisible = true
        ivOff.isVisible = true
        binding?.apply {
            okLayout.isVisible = true
            offLayout.isVisible = true
            okRightLayout.isVisible = true
            offLeftLayout.isVisible = true
        }
//        binding!!.groupOkTopView.isVisible = true
//        llShowTop.visibility = View.VISIBLE
        bringToFront()
//        ivShowMoney.bringToFront()

    }

    private fun showMoneyFormat(money: Int): String {
        if (money < 1000 * 100) {
            return money.formatRealMoney()
        } else if (money < 10000 * 100) {
            if ((money % 100000) == 0) {
                val resultNoDecimal = (money / 100000f).round(0)
                return resultNoDecimal + "k"

            } else if ((money % 10000) == 0) {
                val resultNoDecimal = (money / 100000f).round(1)
                return resultNoDecimal + "k"
            } else {
                val resultNoDecimal = (money / 100000f).round(2)
                return resultNoDecimal + "k"
            }
        } else {
            val tenThousand = money / 1000000
            val thousand = money / 100000 % 10
            val hundred = money / 10000 % 10
            val ten = money / 1000 % 10
            if (thousand > 0) { // 千位有值
                return if (hundred > 0) { // 百位有值
                    if (ten > 0) { // 十位有值
//                        ("${tenThousand}.${thousand}${hundred}W+")
                        ("${tenThousand}.${thousand}${hundred}W")
                    } else { // 十位没有值
                        ("${tenThousand}.${thousand}${hundred}W")
                    }

                } else { // 百位没有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}.${thousand}W")
//                        ("${tenThousand}.${thousand}W+")
                    } else { // 十位没有值
                        ("${tenThousand}.${thousand}W")
                    }
                }

            } else { // 千位没有值
                return if (hundred > 0) { // 百位有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}.0${hundred}W")
//                        ("${tenThousand}.0${hundred}W+")
                    } else { // 十位没有值
                        ("${tenThousand}.0${hundred}W")
                    }

                } else { // 百位没有值
                    if (ten > 0) { // 十位有值
                        ("${tenThousand}W")
//                        ("${tenThousand}W+")
                    } else { // 十位没有值
                        ("${tenThousand}W")
                    }
                }
            }
        }
    }

    /**
     * 保留几位小数并且是截取
     */
    private fun Float.round(decimalPlaces: Int): String {
        if (decimalPlaces < 0) throw IllegalArgumentException()

        val bigDecimal = BigDecimal(this.toString())
        return bigDecimal.setScale(decimalPlaces, RoundingMode.DOWN).stripTrailingZeros()
            .toPlainString()
    }
}