package arch.cayenne.lib.common.ui.gesture

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class TikTokGesture(view: View) : GestureDetector.SimpleOnGestureListener() {

    private val gesture: GestureDetector
    private var listener: TikTokGestureListener? = null

    init {
        gesture = GestureDetector(view.context, this)
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
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
            listener?.onHorizontalScroll(distance)
        }
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    fun setListener(listener: TikTokGestureListener) {
        this.listener = listener
    }

    interface TikTokGestureListener {
        fun onHorizontalFling()
        fun onHorizontalScroll(offsetX: Float)
        fun onActionUp()
    }
}