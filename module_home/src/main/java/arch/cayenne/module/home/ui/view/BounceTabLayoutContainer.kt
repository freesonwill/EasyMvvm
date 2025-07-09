package arch.cayenne.module.home.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class BounceTabLayoutContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var lastX = 0f
    private var isOverScrolling = false
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var startIntercept = false
    private val maxOverScroll by lazy { 60 * resources.displayMetrics.density } // 最大拉動距離 60dp

    /**
     * 是否啟用回彈效果，預設 true。外部可動態設置。
     */
    var enableBounce: Boolean = true

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!enableBounce) return false
        val tabLayout = getChildAt(0) ?: return super.onInterceptTouchEvent(ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                activePointerId = ev.getPointerId(0)
                isOverScrolling = false
                startIntercept = false
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex == -1) return false
                val x = ev.getX(pointerIndex)
                val dx = x - lastX
                if (shouldOverScroll(tabLayout, dx)) {
                    startIntercept = true
                    lastX = x
                    return true
                }
                lastX = x
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!enableBounce) return super.onTouchEvent(event)
        val tabLayout = getChildAt(0) ?: return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                activePointerId = event.getPointerId(0)
                isOverScrolling = false
                startIntercept = false
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex == -1) return false
                val x = event.getX(pointerIndex)
                val dx = x - lastX
                if (startIntercept || shouldOverScroll(tabLayout, dx)) {
                    isOverScrolling = true
                    val newTranslation = tabLayout.translationX + dx / 2
                    tabLayout.translationX = newTranslation.coerceIn(-maxOverScroll, maxOverScroll)
                }
                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isOverScrolling) {
                    tabLayout.animate()
                        .translationX(0f)
                        .setDuration(150)
                        .start()
                    isOverScrolling = false
                }
                activePointerId = MotionEvent.INVALID_POINTER_ID
                startIntercept = false
            }
        }
        return isOverScrolling || startIntercept
    }

    private fun shouldOverScroll(tabLayout: View, dx: Float): Boolean {
        val canScrollLeft = tabLayout.canScrollHorizontally(-1)
        val canScrollRight = tabLayout.canScrollHorizontally(1)
        return (dx > 0 && !canScrollLeft) || (dx < 0 && !canScrollRight)
    }
} 