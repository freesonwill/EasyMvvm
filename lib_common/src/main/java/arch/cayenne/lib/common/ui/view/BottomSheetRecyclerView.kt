package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class BottomSheetRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var lastX = 0f
    private var lastY = 0f

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = e.rawX
                lastY = e.rawY
                parent.requestDisallowInterceptTouchEvent(true)
                isNestedScrollingEnabled = true // 啟用嵌套滾動, 滑動到頂部時能外拋滑動事件讓彈窗收起
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                isNestedScrollingEnabled = false
            }

            MotionEvent.ACTION_MOVE -> {
                val isVerticalScroll = abs(lastY - e.rawY) > abs(lastX - e.rawX)
                if (isVerticalScroll) {
                    val canScrollUp = this.canScrollVertically(-1)
                    if (!canScrollUp && e.rawY > lastY) { // 滑到頂部，且持續上滑 -> 不處理事件，讓彈窗收起
                        parent.requestDisallowInterceptTouchEvent(false)
                    } else if (canScrollUp && e.rawY > lastY) { // 可以上滑，且持續上滑 -> 處理事件，不然會無法滑動
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
        }
        return super.onTouchEvent(e)
    }

}