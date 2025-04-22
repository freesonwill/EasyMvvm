package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 在ViewPager2中做横向滑动时，touch事件被ViewPager2拦截，
 * HorizontalRecyclerView 可以在ViewPager2中做横向滑动，不可滑动后将touch事件还给ViewPager2
 * */
class HorizontalRecyclerView :
    RecyclerView {
    private var startX: Float = 0f
    private var startY: Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                // 先不允许父View拦截，保证RecyclerView能接收到后续事件
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs((ev.x - startX).toDouble()).toFloat()
                val dy = abs((ev.y - startY).toDouble()).toFloat()
                // 判断是否是横向滑动
                if (dx > dy) {
                    val canScrollLeft = canScrollHorizontally(-1)
                    val canScrollRight = canScrollHorizontally(1)
                    // 如果RecyclerView不能向某个方向滑动，则允许父ViewPager2拦截
                    if ((ev.x > startX && !canScrollLeft) ||
                        (ev.x < startX && !canScrollRight)
                    ) {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        if (layoutManager == null) {
            return false
        }

        val lm = layoutManager as LinearLayoutManager?
        return if (direction < 0) {
            // 检查是否可以向左滚动
            lm!!.findFirstVisibleItemPosition() != 0 ||
                    getChildAt(0) != null && getChildAt(0).left < 0
        } else {
            // 检查是否可以向右滚动
            lm!!.findLastVisibleItemPosition() != lm.itemCount - 1 ||
                    getChildAt(lm.childCount - 1) != null &&
                    getChildAt(lm.childCount - 1).right > width
        }
    }
}