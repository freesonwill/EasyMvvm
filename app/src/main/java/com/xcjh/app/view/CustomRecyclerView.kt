package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * 横向滑动的时候左右拦截滑动事件，但是不拦截上下滑动，让列表横向滑动的时候不得切换ViewPager2
 */
class CustomRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

//    private var isParentScrollable = false
//
//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        when (ev.action) {
//            MotionEvent.ACTION_MOVE -> parent.requestDisallowInterceptTouchEvent(!isParentScrollable)
//        }
//        return super.dispatchTouchEvent(ev)
//    }
//
//    fun setParentScrollable(scrollable: Boolean) {
//        isParentScrollable = scrollable
//    }
    private var startX = 0f
    private var startY = 0f
    private var isHorizontalScroll = false

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = e.x
                startY = e.y
                isHorizontalScroll = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - startX
                val dy = e.y - startY
                if (!isHorizontalScroll && Math.abs(dx) > Math.abs(dy)) {
                    isHorizontalScroll = true
                }
                // 如果是水平滑动，则拦截事件；否则，不拦截
                if (isHorizontalScroll) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isHorizontalScroll = false
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(e)
    }


}