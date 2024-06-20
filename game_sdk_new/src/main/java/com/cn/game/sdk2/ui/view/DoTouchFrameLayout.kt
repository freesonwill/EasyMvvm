package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.cn.game.sdk2.ui.helper.ViewHelper.isAdd
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel

/**
 * 首页遮照触摸
 * 用于分发MoneyView超过父布局部分的点击事件
 * 需要关联viewmodel获取当前的MoneyView
 */
class DoTouchFrameLayout : FrameLayout {
    private var mViewModel: Fast3ViewModel? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )


    /**
     * 关联viewmodel 用于获取当前的moneyView
     */
    fun linkViewModel(viewModel: Fast3ViewModel) {
        this.mViewModel = viewModel
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mViewModel?.anchorMoneyView?.get()?.let {
                    if (it.isAdd() && it.llShowTop.isVisible) {
                        val x = event.rawX
                        val y = event.rawY
                        val offLocation = IntArray(2)
                        val okLocation = IntArray(2)
                        it.ivOff.getLocationOnScreen(offLocation)
                        it.ivOk.getLocationOnScreen(okLocation)
                        val offX = offLocation[0]
                        val offY = offLocation[1]
                        val okX = okLocation[0]
                        val okY = okLocation[1]
                        val isTouchOnOff = x >= offX && x <= (offX + it.ivOff.width)
                                && y >= offY && y <= (offY + it.ivOff.height)
                        val isTouchOnOk = x >= okX && x <= (okX + it.ivOk.width)
                                && y >= okY && y <= (okY + it.ivOk.height)
                        if (isTouchOnOff && it.ivOff.isVisible) {
                            it.ivOff.performClick()
                            return true
                        }
                        if (isTouchOnOk && it.ivOk.isVisible) {
                            it.ivOk.performClick()
                            return true
                        }
                        return false
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

}