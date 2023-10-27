package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 不能上下滑动的列表
 */
class NoVScrollRecycleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? =null, defStyleAttr: Int =0)
    : RecyclerView(context, attrs, defStyleAttr) {
    private var lastX = 0f
    private var lastY = 0f
  /*  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(lastY-y) > abs(lastX-x)) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP -> {}
        }
        lastX = x
        lastY = y
        return super.dispatchTouchEvent(ev)
    }*/
}