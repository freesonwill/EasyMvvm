package arch.cayenne.lib.base.ui._interface

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener: View.OnTouchListener {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var startTime: Long = 0L
    private val SWIPE_THRESHOLD = 100 // 按下和抬起的滑动距离
    private val SWIPE_MAX_TIME = 1000 // 按下和抬起的时间间隔
    private val thresholdY = 50 // Y轴滑动阀值

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_DOWN -> {
                if(startTime == 0L){
                    startX = event.x
                    startY = event.y
                    startTime = event.eventTime
                }
                return event.action == MotionEvent.ACTION_DOWN
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val endTime = event.eventTime
                val diffX = endX - startX
                val diffY = endY - startY
                val timeDiff = endTime - startTime
                if (diffX > SWIPE_THRESHOLD && timeDiff <= SWIPE_MAX_TIME&&diffY<=thresholdY) {
                    onSwipeLeft()
                    return true
                }
                startTime = 0
            }
            MotionEvent.ACTION_CANCEL-> {
                startTime = 0
            }
        }
        return false
    }


    open fun onSwipeLeft() {}
}