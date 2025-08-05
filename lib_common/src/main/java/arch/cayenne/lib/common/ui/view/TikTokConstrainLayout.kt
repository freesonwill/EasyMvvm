package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import arch.cayenne.lib.base.ui.gesture.TikTokGestureListener
import arch.cayenne.lib.base.ui.gesture.TikTokInterface
import arch.cayenne.lib.skin.widget.SkinnableConstraintLayout
import kotlin.math.abs

class TikTokConstrainLayout: SkinnableConstraintLayout, TikTokInterface {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    private var onGestureListener: TikTokGestureListener? = null

    private var initialX = 0f
    private var initialY = 0f
    private var lastOffsetX = 0f
    private var isIntercepting = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("abcd", " onInterceptTouchEvent ${ev.action} ${ev.actionMasked}")
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
                lastOffsetX = 0f
                isIntercepting = false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        Log.d("abcd", "event ${ev.action} $isIntercepting")

        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val deltaX = abs(ev.x - initialX)
                val deltaY = abs(ev.y - initialY)
                Log.d("abcd", "event ${ev.action} $deltaX $deltaY $touchSlop    de ${ev.x}  ${ev.y}")
                if (abs(ev.x) > touchSlop && deltaX > deltaY) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    val offsetX = ev.x - initialX - lastOffsetX
                    onGestureListener?.onHorizontalScroll(offsetX)
                    lastOffsetX = offsetX
                    return true
                }
                if (deltaY > touchSlop && deltaY > deltaX * 2) {
                    // 明顯垂直滑動，讓子view處理
                    isIntercepting = false
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 重置狀態，準備迎接下一次觸摸
                initialX = 0f
                initialY = 0f
                lastOffsetX = 0f
                onGestureListener?.onActionUp()
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun setOnGestureListener(listener: TikTokGestureListener) {
        onGestureListener = listener
    }
}