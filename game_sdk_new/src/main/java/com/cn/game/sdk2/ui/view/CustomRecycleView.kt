package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CustomRecycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private var touchX = 0f
    private var touchY = 0f
    private var isHandle = false
    private var isTrigger = false
    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : SimpleOnGestureListener() {

            override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
                val x: Float = p1.x - p0.x
                val y: Float = p1.y - p0.y
                if (abs(x) > abs(y) && abs(x) > 100) return false
                return true
            }

        })

    override fun onTouchEvent(e: MotionEvent): Boolean {
        /*when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = e.x
                touchY = e.y
                isTrigger = false
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_MOVE -> {
                val x: Float = e.x - touchX
                val y: Float = e.y - touchY
                if ((!isTrigger && abs(x) > abs(y) && abs(x) > 10) || isTrigger) {
                    isTrigger = true
                    touchX = e.x
                    touchY = e.y
                    return false
                }
            }
        }*/
        return super.onTouchEvent(e)
    }
    /*override fun onTouchEvent(e: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(e) || super.onTouchEvent(e)
    }*/
}