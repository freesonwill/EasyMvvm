package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.utils.LogUtils

class InnerViewPager2Container @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var viewPager2: ViewPager2? = null
    private var disallowParentInterceptDownEvent = true
    private var startX = 0f
    private var startY = 0f


    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewPager2) {
                viewPager2 = child
                break
            }
        }
        if (viewPager2 == null) {
            throw IllegalStateException("no viewpager2 in InnerViewPager2Container")
        }
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val doNetNeedIntercept =
            (!viewPager2!!.isUserInputEnabled || viewPager2?.adapter == null || viewPager2?.adapter!!.itemCount <= 1)
        if (doNetNeedIntercept) {
            return super.onInterceptTouchEvent(ev)
        }
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = it.x
                    startY = it.y
                    parent.requestDisallowInterceptTouchEvent(!disallowParentInterceptDownEvent)
                }
                MotionEvent.ACTION_MOVE -> {
                    val endX = ev.x
                    val endY = ev.y
                    val disX = Math.abs(endX - startX)
                    val disY = Math.abs(endY - startY)
                    if(viewPager2?.orientation == ViewPager2.ORIENTATION_HORIZONTAL){
                        onHorizontalActionMove(endX,disX,disY)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }


    private fun onHorizontalActionMove(endX: Float, disX: Float, disY: Float) {
        if (viewPager2?.adapter == null) {
            return
        }
        if (disX > disY) {
            val currentItem = viewPager2?.currentItem
            val itemCount = viewPager2?.adapter!!.itemCount
            if (currentItem == 0 && endX - startX > 0) {
                parent.requestDisallowInterceptTouchEvent(false)
            } else {
                val flag1 = currentItem != itemCount - 1
                val flag2 = endX - startX >= 0
                LogUtils.dTag("aaa", "innerviewpager2  flag1 $flag1 flag2 $flag2")
                parent.requestDisallowInterceptTouchEvent(flag1 || flag2)
            }
        } else if (disY > disX) {
            parent.requestDisallowInterceptTouchEvent(false)
        }


    }


}