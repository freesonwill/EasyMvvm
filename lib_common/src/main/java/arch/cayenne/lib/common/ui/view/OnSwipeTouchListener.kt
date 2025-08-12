package arch.cayenne.lib.common.ui.view

import android.view.MotionEvent
import android.view.View

open class OnSwipeTouchListener : View.OnTouchListener {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var startTime: Long = 0L
    private val SWIPE_THRESHOLD = 200 // 按下和抬起的X轴滑动距离
    private val SWIPE_MAX_TIME = 1500 // 按下和抬起的时间间隔
    private val thresholdY = 100 // Y轴滑动阀值

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (startTime == 0L) {
                    startX = event.x
                    startY = event.y
                    startTime = event.eventTime
                }
                return false
            }

            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val endTime = event.eventTime
                val diffX = endX - startX
                val diffY = endY - startY
                val timeDiff = endTime - startTime
                startTime = 0L
                if (diffX > SWIPE_THRESHOLD && timeDiff <= SWIPE_MAX_TIME && diffY < thresholdY) {
                    onSwipeRight()
                    return true
                }
                return false
            }

            MotionEvent.ACTION_CANCEL -> {
                startTime = 0L
            }
        }
        return false
    }


    open fun onSwipeRight() {}
}