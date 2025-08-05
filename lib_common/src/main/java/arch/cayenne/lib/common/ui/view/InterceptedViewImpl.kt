package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.R
import kotlin.math.abs

/**
 * @date: 2025/8/4 18:53
 * @description:手势拦截实现类
 */
class InterceptedViewImpl(private val view: ViewGroup) {
    private val TAG = "InterceptedViewImpl"
    private var isIntercepting = false

    enum class InterceptedDirection(val v: Int) {
        UP(0x01), //上滑
        DOWN(0x02), //下滑
        LEFT(0x04),//左滑
        RIGHT(0x08) //右滑
    }

    private var lastX = -1f
    private var lastY = -1f
    private var interceptFlags: Int = 0
    private var touchSlop: Int = ViewConfiguration.get(view.context).scaledTouchSlop

    fun init(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InterceptedView,
            defStyleAttr,
            0
        ).let { ta ->
            try {
                val interceptedDirection = ta.getInt(R.styleable.InterceptedView_interceptedDirection, 0x00)
                val touchSlop = let {
                    val tv = TypedValue().apply { ta.getValue(R.styleable.InterceptedView_touchSlop, this)}
                    when (tv.type) {
                        TypedValue.TYPE_STRING -> {
                            if (tv.string.toString().equals("DEFAULT", ignoreCase = true)) {
                                touchSlop // 默认值
                            } else {
                                // 其他字符串处理逻辑
                                0
                            }
                        }
                        TypedValue.TYPE_INT_DEC, TypedValue.TYPE_INT_HEX -> {
                            tv.data
                        }
                        else -> 0
                    }
                }
                this.interceptFlags = interceptedDirection
                this.touchSlop = touchSlop
            } finally {
                ta.recycle()
            }
        }
        //"init---$view,interceptFlags:$interceptFlags,touchSlop:$touchSlop".logd(TAG)
    }

    fun setIntercept(d: InterceptedDirection) {
        this.interceptFlags = interceptFlags or d.v
    }

    fun removeIntercept(v: InterceptedDirection) {
        interceptFlags = interceptFlags and (v.v.inv())
    }

    fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        /*"""aaaa----
            ${MotionEvent.actionToString(e.action)},
            e.x:${e.x}, e.y:${e.y}
            e.rawX:${e.rawX}, e.rawY:${e.rawY} 
            lastX:$lastX, lastY:$lastY
            isDirectionScroll:${isDirectionScrolled(e)}, interceptFlags:$interceptFlags, slot:$touchSlop
            canScrollDirection:${canScrollInDirection(e)}
            intercepted:$isIntercepting
            calculateInterceptedType:${calculateInterceptedType(e)}
        """.logd(TAG)*/
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.rawX
                lastY = e.rawY
                isIntercepting = false
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isIntercepting = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isIntercepting
                    && !canScrollInDirection(e) //方向上能否滑动
                    && isDirectionScrolled(e) //
                ) {
                    isIntercepting = true
                }

            }
        }
        return isIntercepting
    }

    fun onTouchEvent(e: MotionEvent): Boolean {
        return isIntercepting
    }

    private fun calculateInterceptedType(e: MotionEvent): Pair<InterceptedDirection, Float> {
        val deltaX = e.rawX - lastX
        val deltaY = e.rawY - lastY
        return when {
            abs(deltaY) > abs(deltaX) -> {
                if (e.rawY < lastY) InterceptedDirection.UP to -deltaY
                else InterceptedDirection.DOWN to deltaY
            }

            else -> {
                if (e.rawX < lastX) InterceptedDirection.LEFT to -deltaX
                else InterceptedDirection.RIGHT to deltaX
            }
        }
    }


    private fun isDirectionScrolled(e: MotionEvent): Boolean {
        val (_, delta) = calculateInterceptedType(e)
        return delta > touchSlop
    }

    private fun canScrollInDirection(e: MotionEvent): Boolean {
        val (direction) = calculateInterceptedType(e)
        return when (direction) {
            InterceptedDirection.UP -> view.canScrollVertically(-1)
            InterceptedDirection.DOWN -> view.canScrollVertically(1)
            InterceptedDirection.LEFT -> view.canScrollHorizontally(-1)
            InterceptedDirection.RIGHT -> view.canScrollHorizontally(1)
        }
    }

}