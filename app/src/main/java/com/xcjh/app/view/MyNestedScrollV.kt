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
        super.onInterceptTouchEvent(ev)
        var intercepted = false
        val x = ev.x
        val y = ev.y
        return ev.action != MotionEvent.ACTION_DOWN
    }*/
}