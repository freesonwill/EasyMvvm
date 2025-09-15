package arch.cayenne.module.bet.ui.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import arch.cayenne.lib.common.ui.view.InterceptedConstraintLayout
import kotlin.math.abs

class BlockSlideConstrainLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InterceptedConstraintLayout(context, attrs, defStyleAttr) {

    private var blockSlideListener: BlockSlideListener? = null
    private var isTouchInBlockingArea = false
    private var downX = 0f
    private var downY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var isDragging = false

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val v = blockSlideListener?.getBlockingRect() ?: return super.onInterceptTouchEvent(e)
        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
            downX = e.x
            downY = e.y
            isDragging = false

            val touchX = e.x
            val touchY = e.y

            val blockingViewScreenLocation = IntArray(2)
            v.getLocationOnScreen(blockingViewScreenLocation)
            val blockingViewScreenRect = Rect(
                blockingViewScreenLocation[0],
                blockingViewScreenLocation[1],
                blockingViewScreenLocation[0] + v.width,
                blockingViewScreenLocation[1] + v.height
            )

            val thisLayoutScreenLocation = IntArray(2)
            this.getLocationOnScreen(thisLayoutScreenLocation)

            val screenTouchX = thisLayoutScreenLocation[0] + touchX
            val screenTouchY = thisLayoutScreenLocation[1] + touchY

            isTouchInBlockingArea = blockingViewScreenRect.contains(screenTouchX.toInt(), screenTouchY.toInt())
            if (isTouchInBlockingArea) {
                this.isEnabled = false
                parent.requestDisallowInterceptTouchEvent(true)
            }
        } else if (e.actionMasked == MotionEvent.ACTION_UP || e.actionMasked == MotionEvent.ACTION_CANCEL) {
            isTouchInBlockingArea = false
            isDragging = false
            this.isEnabled = true
            parent.requestDisallowInterceptTouchEvent(false)
        } else if (e.actionMasked == MotionEvent.ACTION_MOVE) {
            if (isTouchInBlockingArea && !isDragging) {
                val dx = abs(e.x - downX)
                val dy = abs(e.y - downY)
                if (dx > touchSlop || dy > touchSlop) {
                    isDragging = true
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isDragging) { // This ViewGroup is handling the drag
            when (event.actionMasked) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    isTouchInBlockingArea = false
                    isDragging = false
                    this.isEnabled = true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setBlockSlideListener(listener: BlockSlideListener) {
        this.blockSlideListener = listener
    }

    interface BlockSlideListener {
        fun getBlockingRect(): View?
    }
}