package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.cn.game.sdk2.utils.ext.ViewExt.isInArea

/**
 * 首页遮照触摸
 * 用于分发MoneyView超过父布局部分的点击事件
 * 需要关联viewmodel获取当前的MoneyView
 */
class DoTouchFrameLayout : FrameLayout {
    private var anchorMoneyView: MoneyOKView? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    fun setAnchorMoneyView(anchorMoneyView: MoneyOKView) {
        this.anchorMoneyView = anchorMoneyView
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> {
                anchorMoneyView?.let {
                    val x = event.rawX
                    val y = event.rawY
//                    if (it.llShowTop.isVisible) {
                    return if (it.binding!!.okLayout.isInArea(x, y)) {
                        it.binding!!.okLayout.dispatchTouchEvent(event)
                        true
                    } else if (it.binding!!.offLayout.isInArea(x, y)) {
                        it.binding!!.offLayout.dispatchTouchEvent(event)
                        true
                    } else if (it.binding!!.offLeftLayout.isInArea(x, y)) {
                        it.binding!!.offLeftLayout.dispatchTouchEvent(event)
                        true
                    } else if (it.binding!!.okRightLayout.isInArea(x, y)) {
                        it.binding!!.okRightLayout.dispatchTouchEvent(event)
                        true
                    } else {
                        it?.resetAnim()
                        false
                    }
                }
//                }
            }

            MotionEvent.ACTION_CANCEL -> {
                anchorMoneyView?.resetAnim()
            }
        }
        return super.onTouchEvent(event)
    }

}