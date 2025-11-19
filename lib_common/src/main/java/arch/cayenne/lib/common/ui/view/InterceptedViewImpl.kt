package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.ui.view._interface.IInterceptedView
import arch.cayenne.lib.common.ui.view._interface.Direction
import arch.cayenne.lib.common.utils.ext.getTouchListener
import kotlin.math.abs

/**
 * @date: 2025/8/4 18:53
 * @description:手势拦截实现类
 */
class InterceptedViewImpl(private val view: ViewGroup) : IInterceptedView {
    private val TAG = "InterceptedViewImpl"
    private var isIntercepting = false
    private var downEventSnapshot: MotionEventSnapshot? = null
    private lateinit var canScrollView: View
    private var lastX = -1f
    private var lastY = -1f
    private var interceptFlags: Int = 0
    private var touchSlop: Int = 0
    private var canScrollViewId = View.NO_ID

    private val canScrollView2:View get() {
        var canScrollView = if (canScrollViewId == View.NO_ID) view else view.findViewById<View>(canScrollViewId)
        if (canScrollView is ViewPager2) {
            val recyclerView = canScrollView.getChildAt(0) as? RecyclerView
            canScrollView = recyclerView
        }
        return canScrollView
    }

    fun init(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        "InterceptedViewImpl----------init-------view:$view".logd(TAG)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InterceptedView,
            defStyleAttr,
            0
        ).let { ta ->
            try {
                val interceptedDirection = ta.getInt(R.styleable.InterceptedView_interceptedDirection, 0x00)
                val touchSlop = let {
                    val tv = TypedValue().apply { ta.getValue(R.styleable.InterceptedView_touchSlop, this) }
                    val defaultTouchSlop = ViewConfiguration.get(view.context).scaledTouchSlop
                    when (tv.type) {
                        TypedValue.TYPE_STRING -> {
                            if (tv.string.toString().equals("DEFAULT", ignoreCase = true)) {
                                defaultTouchSlop // 默认值
                            } else {
                                // 其他字符串处理逻辑
                                defaultTouchSlop
                            }
                        }

                        TypedValue.TYPE_INT_DEC, TypedValue.TYPE_INT_HEX -> tv.data
                        else -> defaultTouchSlop
                    }
                }
                val canScrollViewId = ta.getResourceId(R.styleable.InterceptedView_canScrollView, View.NO_ID)
                this.interceptFlags = interceptedDirection
                this.touchSlop = touchSlop
                "InterceptedViewImpl----------canScrollViewId:$canScrollViewId-------touchSlop:$touchSlop,view:$view".logd(TAG)
                if(canScrollViewId == View.NO_ID) {
                    this.canScrollView = view
                }else {
                    view.doOnAttach {
                        var canScrollView = view.findViewById<View>(canScrollViewId)
                        if (canScrollView is ViewPager2) {
                            val recyclerView = canScrollView.getChildAt(0) as RecyclerView
                            canScrollView = recyclerView
                        }
                        this.canScrollView = canScrollView
                        "InterceptedViewImpl----------canScrollView:$canScrollView,view:$view".logd(TAG)
                    }
                }
            } finally {
                ta.recycle()
            }
        }
        //"init---$view,interceptFlags:$interceptFlags,touchSlop:$touchSlop".logd(TAG)
    }

    override fun getInterceptedDirections(): List<Direction> {
        return Direction.entries.filter { interceptFlags and it.v != 0 }
    }

    override fun setInterceptedDirection(first: Direction, vararg other: Direction) {
        interceptFlags = other.fold(first.v) { acc, dir ->
            acc or dir.v
        }
    }

    fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        //"canScrollView2:${canScrollView2},canScrollViewId:$canScrollViewId,view:$view".logd(TAG)
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.rawX
                lastY = e.rawY
                isIntercepting = false
                downEventSnapshot = e.toSnapshot()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isIntercepting = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isIntercepting
                    && !canScrollInDirection(e) //方向上能否滑动
                    && isInterceptConditionMet(e) //是否满足拦截条件
                ) {
                    isIntercepting = true
                    //因为在Move事件中拦截，所以需要发送一个假的DOWN事件给TouchListener
                    sendFakeDownEventToTouchListener()
                }

            }
        }
        /*"""aaaa----
            ${MotionEvent.actionToString(e.action)},
            e.x:${e.x}, e.y:${e.y}
            e.rawX:${e.rawX}, e.rawY:${e.rawY}
            lastX:$lastX, lastY:$lastY
            isDirectionScroll:${isInterceptConditionMet(e)}, interceptFlags:$interceptFlags, slot:$touchSlop
            canScrollDirection:${canScrollInDirection(e)}
            intercepted:$isIntercepting
            calculateInterceptedType:${calculateInterceptedType(e)}
        """.logd(TAG)*/
        return isIntercepting
    }

    fun onTouchEvent(e: MotionEvent): Boolean {
        return isIntercepting
    }

    /**
     * 发送一个假的DOWN事件给TouchListener
     */
    private fun sendFakeDownEventToTouchListener() {
        downEventSnapshot!!.toMotionEvent().apply {
            view.getTouchListener()?.onTouch(view, this)
            recycle()
        }
    }

    /**
     * 计算当前手势的拦截类型和偏移量
     * @param e MotionEvent
     * @return Pair<InterceptedDirection, Float> 拦截方向和偏移量
     */
    private fun calculateInterceptedType(e: MotionEvent): Pair<Direction, Float> {
        val deltaX = e.rawX - lastX
        val deltaY = e.rawY - lastY
        return when {
            abs(deltaY) > abs(deltaX) -> {
                if (e.rawY < lastY) Direction.UP to -deltaY
                else Direction.DOWN to deltaY
            }

            else -> {
                if (e.rawX < lastX) Direction.LEFT to -deltaX
                else Direction.RIGHT to deltaX
            }
        }
    }

    /**
     * 判断是否满足拦截条件
     * @param e MotionEvent
     * @return true表示满足拦截条件，false表示不满足
     */
    private fun isInterceptConditionMet(e: MotionEvent): Boolean {
        val (direction, delta) = calculateInterceptedType(e)
        return shouldIntercept(direction) && delta > touchSlop
    }

    /**
     * 判断当前View是否可以在指定方向上滚动，不能滚动才去截获
     * @param e MotionEvent
     * @return true表示可以滚动，false表示不能滚动
     */
    private fun canScrollInDirection(e: MotionEvent): Boolean {
        val (direction) = calculateInterceptedType(e)
        return when (direction) {
            Direction.UP -> canScrollView.canScrollVertically(1) //是否还能上滑
            Direction.DOWN -> canScrollView.canScrollVertically(-1) //是否下滑
            Direction.LEFT -> canScrollView.canScrollHorizontally(1) //是否还能左滑
            Direction.RIGHT -> canScrollView.canScrollHorizontally(-1) //是否还能右滑
            else -> throw IllegalStateException("Unsupported direction: $direction")
        }
    }

    /**
     * 判断是否拦截指定方向的手势
     * @param direction
     * @return
     */
    private fun shouldIntercept(direction: Direction): Boolean {
        return (interceptFlags and direction.v) != 0
    }

    data class MotionEventSnapshot(
        val action: Int,
        val downTime: Long,
        val eventTime: Long,
        val x: Float,
        val y: Float,
        val metaState: Int
    )

    private fun MotionEvent.toSnapshot(): MotionEventSnapshot {
        return MotionEventSnapshot(
            action = this.action,
            downTime = this.downTime,
            eventTime = this.eventTime,
            x = this.x,
            y = this.y,
            metaState = this.metaState
        )
    }

    private fun MotionEventSnapshot.toMotionEvent(): MotionEvent {
        return MotionEvent.obtain(
            downTime,
            eventTime,
            action,
            x,
            y,
            metaState
        )
    }
}