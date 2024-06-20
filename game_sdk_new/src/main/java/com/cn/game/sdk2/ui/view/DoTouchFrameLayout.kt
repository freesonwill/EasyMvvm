package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
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

    fun setAnchorMoneyView(anchorMoneyView: MoneyOKView){
        this.anchorMoneyView = anchorMoneyView
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                anchorMoneyView?.let {
                    val x = event.rawX
                    val y = event.rawY
                    if (it.llShowTop.isVisible) {
                        return if (it.ivOff.isInArea(x,y) && it.ivOff.isVisible) {
                            it.ivOff.performClick()
                            true
                        } else if (it.ivOk.isInArea(x,y) && it.ivOk.isVisible) {
                            it.ivOk.performClick()
                            true
                        } else {
                            false
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

}