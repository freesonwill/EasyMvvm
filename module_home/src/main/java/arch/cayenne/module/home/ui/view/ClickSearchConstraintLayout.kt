package arch.cayenne.module.home.ui.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import arch.cayenne.lib.skin.widget.SkinnableConstraintLayout
import kotlin.math.abs

class ClickSearchConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SkinnableConstraintLayout(context, attrs, defStyleAttr) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var downX = 0f
    private var downY = 0f
    private var potentialTargetChild: View? = null

    private var listener: ((View) -> Unit)? = null
    fun setOnChildClickedInterceptedListener(listener: (View) -> Unit) {
        this.listener = listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                potentialTargetChild = findTargetChild(ev)
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(ev.x - downX)
                val dy = abs(ev.y - downY)
                if (dx > touchSlop || dy > touchSlop) {
                    potentialTargetChild = null
                }
                return false
            }
            MotionEvent.ACTION_UP -> {
                if (potentialTargetChild != null) {
                    Log.w("INTERCEPT", "攔截條件滿足！正在攔截... 父層將處理這次點擊。")
                    listener?.invoke(potentialTargetChild!!)
                    potentialTargetChild = null
                }
                return false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
    private fun findTargetChild(ev: MotionEvent): View? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val rect = Rect()
            child.getHitRect(rect)
            if (rect.contains(ev.x.toInt(), ev.y.toInt())) {
                return child
            }
        }
        return null
    }
}