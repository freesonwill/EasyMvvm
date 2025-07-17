package arch.cayenne.lib.common.utils.helper.toastGesture

import android.view.MotionEvent
import android.view.View
import arch.cayenne.lib.common.utils.ViewUtils

class ToastSlideGesture: ToastGesture() {

    private var downY = 0f
    private var lastY = 0f
    private var isDragging = false
    private var lastMoveTime = 0L
    private var lastMoveY = 0f
    private var pendingRemove = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = event.rawY
                lastY = view.translationY
                isDragging = false
                lastMoveTime = event.eventTime
                lastMoveY = event.rawY
                pendingRemove = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = event.rawY - downY
                if (deltaY < -10) isDragging = true
                if (isDragging && deltaY < 0) {
                    view.translationY = lastY + deltaY
                }
                lastMoveTime = event.eventTime
                lastMoveY = event.rawY
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    if (pendingRemove) {
                        removeAnimation(view)
                        pendingRemove = false
                        return true
                    }
                    val timeDiff = event.eventTime - lastMoveTime
                    val yDiff = event.rawY - lastMoveY
                    val velocity = if (timeDiff > 0) yDiff / timeDiff * 1000 else 0f // px/s
                    val threshold = -view.height / 3f
                    if (view.translationY < threshold || velocity < -1000) {
                        removeAnimation(view)
                    } else {
                        // 距離不夠，自動回彈
                        resetPosition(view)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun canAutoRemove(): Boolean {
        return if (isDragging) {
            pendingRemove = true
            false
        } else {
            true
        }
    }

    private fun removeAnimation(view: View) {
        val statusBar = ViewUtils.getStatusBarHeight(view.context)
        val height = view.height + statusBar
        view.animate()
            .translationY(-height.toFloat())
            .setDuration(200)
            .withEndAction {
                forceRemove()
            }
            .start()
    }

    private fun resetPosition(view: View) {
        val statusBar = ViewUtils.getStatusBarHeight(view.context)
        view.animate()
            .translationY(statusBar.toFloat())
            .setDuration(200)
            .start()
    }
}