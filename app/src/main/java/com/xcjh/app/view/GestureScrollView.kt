package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import kotlin.math.abs

class GestureScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? =null, defStyleAttr: Int =0)
    :ScrollView(context, attrs, defStyleAttr) {
    private var lastX = 0f
    private var lastY = 0f

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if(e == null){
            return super.onInterceptTouchEvent(e)
        }

        var intercept = super.onInterceptTouchEvent(e)

        when (e.action) {

            MotionEvent.ACTION_DOWN -> {
                lastX = e.x
                lastY = e.y
            }
            MotionEvent.ACTION_MOVE -> {
                // 只要横向大于竖向，就拦截掉事件。
                // 部分机型点击事件（slopx==slopy==0），会触发MOVE事件。
                // 所以要加判断(slopX > 0 || sloy > 0)
                val slopX = abs(e.x - lastX)
                val slopY = abs(e.y - lastY)
                if ((slopX > 0 || slopY > 0) && slopX <= slopY) {
                    requestDisallowInterceptTouchEvent(true)
                    intercept = true
                }else{
                    intercept = false
                }

            }
            MotionEvent.ACTION_UP -> intercept = false
        }
        return intercept
    }
}