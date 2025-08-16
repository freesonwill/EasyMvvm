package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import arch.cayenne.lib.common.ui.view._interface.IInterceptedView
import arch.cayenne.lib.common.ui.view._interface.Direction
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout

/**
 * @date: 2025/8/4 17:23
 * @description: 支持手势拦截ConstraintLayout
 */
class InterceptedLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SkinnableLinearLayout(context, attrs, defStyleAttr), IInterceptedView {
    private var impl: InterceptedViewImpl = InterceptedViewImpl(this)

    init {
        impl.init(context, attrs, defStyleAttr)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return impl.onInterceptTouchEvent(e) || super.onInterceptTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        return impl.onTouchEvent(e) || super.onTouchEvent(e)
    }

    override fun getInterceptedDirections(): List<@Direction.Flag Int> {
        return impl.getInterceptedDirections()
    }

    override fun setInterceptedDirection(@Direction.Flag direction: Int) {
        impl.setInterceptedDirection(direction)
    }
}