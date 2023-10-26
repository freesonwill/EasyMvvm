package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlin.math.abs

class NoVScrollRecycleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? =null, defStyleAttr: Int =0)
    : RecyclerView(context, attrs, defStyleAttr) {
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
                parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_MOVE -> {
                val slopX = abs(e.x - lastX)
                val slopY = abs(e.y - lastY)
                if (slopY > 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
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