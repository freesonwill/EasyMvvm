package arch.cayenne.lib.base.ui._interface

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

// OnSwipeTouchListener.kt
open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            // 检测向右滑动到屏幕最左侧
            val deltaX = e2.x - e1.x
            val isAtLeftEdge = e1.x < 100 // 屏幕左侧 100px 范围内
            if (deltaX > 200 && velocityX > 200 && isAtLeftEdge) {
                // 触发返回上一级页面
                onSwipeLeft()
                return true
            }
            return false
        }
    }

    open fun onSwipeLeft() {}
}