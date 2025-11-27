package com.walisport.module.hall.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

class ViewPager2PriorityRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var initialX = 0f
    private var initialY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = e.x
                initialY = e.y
                // 一按下就请求父 ViewPager2 不要拦截，让 RecyclerView 先处理
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = e.x - initialX
                val dy = e.y - initialY

                // 还没脱离触摸容差，不做任何判断
                if (kotlin.math.abs(dx) <= touchSlop && kotlin.math.abs(dy) <= touchSlop) {
                    return super.dispatchTouchEvent(e)
                }

                // 水平滑动
                if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                    val isAtStart = !canScrollHorizontally(-1)  // 已经在最左边
                    val isAtEnd = !canScrollHorizontally(1)    // 已经在最右边
                    // 只有在滑到头且滑动方向是继续往头外滑，才放手给 ViewPager2
                    if ((isAtStart && dx > 0) || (isAtEnd && dx < 0)) {
                        // 放手，让 ViewPager2 接管
                        parent.requestDisallowInterceptTouchEvent(false)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                } else {
                    // 垂直滑动为主，直接放手（如果是垂直 ViewPager2 就由它处理）
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(e)
    }
}