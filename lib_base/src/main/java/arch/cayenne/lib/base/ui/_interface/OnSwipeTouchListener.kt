package arch.cayenne.lib.base.ui._interface

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {


    private var startX: Float = 0f
    private var startTime: Long = 0L
    private val SWIPE_THRESHOLD = 100 // Minimum distance in pixels
    private val SWIPE_MAX_TIME = 1000 // Max time in milliseconds (1 second)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startTime = event.eventTime
                return true
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endTime = event.eventTime
                val diffX = endX - startX
                val timeDiff = endTime - startTime

                // Check for left-to-right swipe: distance >= 100 pixels, within 1 second, mostly horizontal
                if (diffX > SWIPE_THRESHOLD && timeDiff <= SWIPE_MAX_TIME) {
                    onSwipeLeft()
                    return true
                }
            }
        }
        return false
    }


    open fun onSwipeLeft() {}
}