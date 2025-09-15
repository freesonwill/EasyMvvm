package arch.cayenne.lib.base.ui.gesture

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class TikTokGesture(private val view: View) : GestureDetector.SimpleOnGestureListener() {

    private val gesture: GestureDetector = GestureDetector(view.context, this)
    private var listener: TikTokGestureListener? = null

    init {
        view.setOnTouchListener { v, event ->
            if (!v.isEnabled) return@setOnTouchListener false
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                view.parent.requestDisallowInterceptTouchEvent(false)
                listener?.onActionUp()
            }
            gesture.onTouchEvent(event)
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 == null)  return super.onFling(null, e2, velocityX, velocityY)
        if (abs(e2.rawX - e1.rawX) > abs(e2.rawY - e1.rawY)) {
            val timeDiff = e2.eventTime - e1.eventTime
            val xDiff = e2.rawX - e1.rawX
            val velocity = if (timeDiff > 0) xDiff / timeDiff * 2000 else 0f // px/s
            if (velocity > 5000) {
                listener?.onFlingToRight()
                return true
            }

        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (e1 == null) return super.onScroll(e1, e2, distanceX, distanceY)
        val isHorizontal = abs(distanceX) > abs(distanceY)
        if (isHorizontal && e2.x > e1.x) {
            val distance = e2.x - e1.x
            view.parent.requestDisallowInterceptTouchEvent(true)
            listener?.onHorizontalScroll(distance)
            return true
        }
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    fun setListener(listener: TikTokGestureListener) {
        this.listener = listener
    }

    interface TikTokGestureListener {
        fun onFlingToRight()
        fun onHorizontalScroll(offsetX: Float)
        fun onActionUp()
    }
}