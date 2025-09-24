package arch.cayenne.lib.common.ui.view

import android.view.MotionEvent
import android.view.View
import arch.cayenne.lib.base.utils.LogUtils

open class OnSwipeTouchListener : View.OnTouchListener {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var startTime: Long = 0L
    private val SWIPE_THRESHOLD = 140 // X轴滑动距离阈值
    private val SWIPE_MAX_TIME = 1600 // 时间间隔
    private val MAX_ANGLE_DEGREES = 45 // 最大允许角度

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
                val diffX = kotlin.math.abs(endX - startX)
                val diffY = kotlin.math.abs(endY - startY)
                val timeDiff = endTime - startTime

                // 计算滑动角度（弧度转角度）
                val angle = Math.toDegrees(kotlin.math.atan2(diffY.toDouble(), diffX.toDouble())).toFloat()
                // LogUtils.e("OnSwipeTouchListener: diffX=$diffX, diffY=$diffY, angle=$angle, timeDiff=$timeDiff")

                startTime = 0L
                // 判断：X轴滑动距离足够，时间符合，角度小于阈值
                if (startX < endX && diffX > SWIPE_THRESHOLD && timeDiff <= SWIPE_MAX_TIME && angle < MAX_ANGLE_DEGREES) {
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

    //获取弧度转角度滑动距离
    fun calculateAngle(diffX: Float, diffY: Float) : Double {
        return (kotlin.math.atan2(diffY, diffX) * (SWIPE_THRESHOLD / Math.PI))
    }

    open fun onSwipeRight() {}
}