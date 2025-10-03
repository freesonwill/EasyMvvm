package com.walisport.module.live.ui.widget

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.constraintlayout.widget.ConstraintLayout;
import arch.cayenne.lib.base.utils.LogUtils
import kotlin.math.abs

class NonInterceptingConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var isVerticalScroll: Boolean = false
    private var scrollDirection: ScrollDirection = ScrollDirection.NONE

    private var isTopScroll: Boolean = true //上滑是否拦截
    private var isDowScroll: Boolean = true //下滑是否拦截
    // 滑动方向的枚举
    enum class ScrollDirection {
        NONE, UP, DOWN
    }

    companion object {
        private const val TOUCH_SLOP = 8f // 滑动阈值，单位像素
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.onInterceptTouchEvent(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录触摸起点
                startX = ev.x
                startY = ev.y
                isVerticalScroll = false
                scrollDirection = ScrollDirection.NONE
                return false // 不拦截 ACTION_DOWN，让子视图有机会接收
            }
            MotionEvent.ACTION_MOVE -> {
                // 计算滑动距离
                val deltaX = abs(ev.x - startX)
                val deltaY = ev.y - startY // 不取绝对值，以便判断方向

                // 判断是否为垂直滑动
                if (abs(deltaY) > deltaX && abs(deltaY) > TOUCH_SLOP) {
                    isVerticalScroll = true
                    // 判断滑动方向
                    scrollDirection = if (deltaY > 0) {
                       // LogUtils.e("MainLayout---onInterceptTouchEvent----------下滑动")
                        ScrollDirection.DOWN // Y 增大，表示向下滑动
                        return isDowScroll
                    } else {
                      //  LogUtils.e("MainLayout---onInterceptTouchEvent-----------上滑动${isTopScroll} ")
                        ScrollDirection.UP // Y 减小，表示向上滑动
                        return isTopScroll //true 拦截不下发 false 表示下发
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isVerticalScroll = false
                scrollDirection = ScrollDirection.NONE
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.onTouchEvent(ev)

        if (isVerticalScroll) {
            // 检测到垂直滑动，不消费事件，返回 false 让父布局处理
            when (ev.action) {
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> return false // 不消费垂直滑动事件，回传给父布局
            }
        }
        // 非垂直滑动事件，交给父类处理
        return super.onTouchEvent(ev)
    }

    fun setIsDowScroll(scroll: Boolean) {
        this.isDowScroll = scroll
    }

    fun setIsTopScroll(scroll: Boolean) {
        this.isTopScroll = scroll
    }

    // 检查是否被请求不拦截触摸事件
    private fun isDisallowInterceptRequested(): Boolean {
        return parent?.let {
            try {
                it::class.java.getMethod("getDisallowIntercept").invoke(it) as? Boolean
            } catch (e: Exception) {
                false
            }
        } ?: false
    }
}