package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

/**
 *
 */
class MyNestedScrollV @JvmOverloads constructor(context: Context, attrs: AttributeSet? =null, defStyleAttr: Int =0)
    : NestedScrollView(context, attrs, defStyleAttr) {
    private var mLastXIntercept = 0f
    private var mLastYIntercept = 0f
  /*  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = false
        val x = ev.x
        val y = ev.y
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                //初始化mActivePointerId
                super.onInterceptTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {

                //横坐标位移增量
                val deltaX = x - mLastXIntercept
                //纵坐标位移增量
                val deltaY = y - mLastYIntercept
                intercepted = abs(deltaX) < abs(deltaY)
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }
        mLastXIntercept = x
        mLastYIntercept = y
        return intercepted
    }*/
}