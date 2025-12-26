package arch.cayenne.lib.common.ui.view
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2

/**
 * @author: wenxi
 * @date: 25/12/25 15:49
 * @description:
 *   使用InnerViewPager2Container包裹ViewPager2，解决ViewPager2中包含了ViewPager2滑动冲突。
 *   始终将InnerViewPager2Container区域内的滑动事件交给内部的ViewPager2处理。
 */

class OnlyInnerViewPager2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var viewPager2: ViewPager2? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (viewPager2 != null) return
        findViewPager()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 始终拦截触摸事件，交给子View（内部ViewPager2）处理
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 将触摸事件传递给内部的ViewPager2
        viewPager2?.dispatchTouchEvent(event)
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 在分发触摸事件时，阻止父View拦截
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 按下时阻止父View拦截
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    // 移动时持续阻止父View拦截
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 抬起或取消时才允许父View拦截
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (viewPager2 != null) return
        findViewPager()
    }

    private fun findViewPager() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewPager2) {
                viewPager2 = child
                break
            }
        }
        if (viewPager2 == null) {
            throw IllegalStateException("no viewpager2 in InnerViewPager2Container")
        }
    }

    override fun onDetachedFromWindow() {
        viewPager2 = null
        super.onDetachedFromWindow()
    }
}