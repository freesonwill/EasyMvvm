package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import arch.cayenne.lib.skin.widget.SkinnableRecyclerView

/**
 * @date: 2025/8/4 17:23
 * @description:支持手势拦截RecyclerView
 */
class InterceptedRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SkinnableRecyclerView(context, attrs, defStyleAttr) {
    private var impl: InterceptedViewImpl = InterceptedViewImpl(this)

    init {
        impl.init(context, attrs, defStyleAttr)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return impl.onInterceptTouchEvent(e) || super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return impl.onTouchEvent(e) || super.onTouchEvent(e)
    }
}