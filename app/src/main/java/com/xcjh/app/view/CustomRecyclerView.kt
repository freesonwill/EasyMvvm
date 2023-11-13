package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * 横向滑动的时候拦截滑动事件，让列表横向滑动的时候不得切换ViewPager2
 */
class CustomRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private var isParentScrollable = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> parent.requestDisallowInterceptTouchEvent(!isParentScrollable)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setParentScrollable(scrollable: Boolean) {
        isParentScrollable = scrollable
    }
}