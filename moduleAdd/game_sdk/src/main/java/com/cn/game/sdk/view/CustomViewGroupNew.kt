package com.cn.game.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

class CustomViewGroupNew (context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // 获取触摸事件的坐标
        val x = ev.x
        val y = ev.y

        // 判断触摸事件是否在父布局的范围内
        val isTouchInsideParent = isTouchInsideView(ev, this)

        // 如果触摸事件在父布局范围内，则继续分发给子视图处理
        if (isTouchInsideParent) {
            return super.dispatchTouchEvent(ev)
        } else {
            // 如果触摸事件在父布局范围之外，则判断是否在某个子视图范围内
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (isTouchInsideView(ev, child)) {
                    // 如果触摸事件在子视图范围内，则继续分发给该子视图处理
                    return super.dispatchTouchEvent(ev)
                }
            }
            // 如果触摸事件不在任何子视图范围内，则拦截触摸事件
            return true
        }
    }

    private fun isTouchInsideView(ev: MotionEvent, view: View): Boolean {
        val x = ev.x
        val y = ev.y
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]
        val viewWidth = view.width
        val viewHeight = view.height
        return !(x < viewX || x > viewX + viewWidth || y < viewY || y > viewY + viewHeight)
    }

}