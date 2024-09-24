package com.cn.game.sdk2.ui.xpopup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import com.lxj.xpopup.enums.LayoutStatus
import com.lxj.xpopup.util.XPopupUtils
import kotlin.math.pow
import kotlin.math.sqrt

open class OriSmartDragLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent {
    private var child: View? = null
    internal var scroller: OverScroller? = null
    private var tracker: VelocityTracker? = null
    internal var enableDrag = true //是否启用手势拖拽
    private var dismissOnTouchOutside = true
    internal var isUserClose = false
    internal var isThreeDrag = false //是否开启三段拖拽
    internal var status = LayoutStatus.Close
    private var duration = 400
    internal var maxY = 0
    internal var minY = 0
    private var lastHeight = 0
    private var touchX = 0f
    private var touchY = 0f
    internal var isScrollUp = false

    var listener: OnCloseListener? = null

    interface OnCloseListener {
        fun onClose()

        fun onDrag(y: Int, percent: Float, isScrollUp: Boolean)

        fun onOpen()
    }

    init {
        if(enableDrag) {
            scroller = OverScroller(context)
        }
    }

    override fun onViewAdded(c: View?) {
        super.onViewAdded(c)
        child = c
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (enableDrag) {
            if (child == null) return
            maxY = child!!.measuredHeight
            minY = 0
            val l: Int = measuredWidth / 2 - child!!.measuredWidth / 2
            child!!.layout(
                l,
                measuredHeight,
                l + child!!.measuredWidth,
                measuredHeight + maxY
            )
            if (status == LayoutStatus.Open) {
                if (isThreeDrag) {
                    //通过scroll上移
                    scrollTo(scrollX, scrollY - (lastHeight - maxY))
                } else {
                    //通过scroll上移
                    scrollTo(scrollX, scrollY - (lastHeight - maxY))
                }
            }
            lastHeight = maxY
        } else {
            val l: Int = measuredWidth / 2 - child!!.measuredWidth / 2
            child!!.layout(
                l,
                measuredHeight - child!!.measuredHeight,
                l + child!!.measuredWidth,
                measuredHeight
            )
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        isUserClose = true
        if (status == LayoutStatus.Closing || status == LayoutStatus.Opening) return false
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (status == LayoutStatus.Closing || status == LayoutStatus.Opening) return false
        if (enableDrag && (scroller!!.computeScrollOffset() || status == LayoutStatus.Close)) {
            touchX = 0f
            touchY = 0f
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (enableDrag) {
                    if (tracker != null) tracker!!.clear()
                    tracker = VelocityTracker.obtain()
                }
                touchX = event.x
                touchY = event.y
            }

            MotionEvent.ACTION_MOVE -> if (enableDrag && tracker != null) {
                tracker!!.addMovement(event)
                tracker!!.computeCurrentVelocity(1000)
                val dy: Int = (event.y - touchY).toInt()
                scrollTo(scrollX, scrollY - dy)
                touchY = event.y
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // click in child rect
                val rect = Rect()
                child!!.getGlobalVisibleRect(rect)
                if (!XPopupUtils.isInRect(event.rawX, event.rawY, rect) && dismissOnTouchOutside) {
                    val distance = sqrt((event.x - touchX).pow(2f) + (event.y - touchY).pow(2f))
                        .toFloat()
                    if (distance < ViewConfiguration.get(context).scaledTouchSlop) {
                        performClick()
                    }
                }
                if (enableDrag && tracker != null) {
                    val yVelocity = tracker!!.yVelocity
                    if (yVelocity > 1500 && !isThreeDrag) {
                        close()
                    } else {
                        finishScroll()
                    }
                    //                    tracker.recycle();
                    tracker = null
                }
            }
        }
        return enableDrag
    }

    protected fun finishScroll() {
        if (enableDrag) {
            val threshold = if (isScrollUp) (maxY - minY) / 3 else (maxY - minY) * 2 / 3
            var dy: Int = (if (scrollY > threshold) maxY else minY) - scrollY
            if (isThreeDrag) {
                val per = maxY / 3
                dy = if (scrollY > per * 2.5f) {
                    maxY - scrollY
                } else if (scrollY <= per * 2.5f && scrollY > per * 1.5f) {
                    per * 2 - scrollY
                } else if (scrollY > per) {
                    per - scrollY
                } else {
                    minY - scrollY
                }
            }
            scroller!!.startScroll(scrollX, scrollY, 0, dy, duration)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        var tempY = y
        if (tempY > maxY) tempY = maxY
        if (tempY < minY) tempY = minY
        val fraction = (tempY - minY) * 1f / (maxY - minY)
        isScrollUp = tempY > scrollY
        if (listener != null) {
            if (isUserClose && fraction == 0f && status != LayoutStatus.Close) {
                status = LayoutStatus.Close
                listener?.onClose()
            } else if (fraction == 1f && status != LayoutStatus.Open) {
                status = LayoutStatus.Open
                listener?.onOpen()
            }
            listener?.onDrag(tempY, fraction, isScrollUp)
        }
        super.scrollTo(x, tempY)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller!!.computeScrollOffset()) {
            scrollTo(scroller!!.currX, scroller!!.currY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isScrollUp = false
        isUserClose = false
        translationY = 0f
    }

    open fun open() {
        post(Runnable {
            val dy: Int = maxY - scrollY
            smoothScroll(if (enableDrag && isThreeDrag) dy / 3 else dy, true)
            status = LayoutStatus.Opening
        })
    }

    open fun close() {
        isUserClose = true
        post(Runnable {
            scroller!!.abortAnimation()
            smoothScroll(minY - scrollY, false)
            status = LayoutStatus.Closing
        })
    }

    protected fun smoothScroll(dy: Int, isOpen: Boolean) {
        scroller!!.startScroll(
            scrollX,
            scrollY,
            0,
            dy,
            (if (isOpen) duration.toFloat() else duration * 0.8f).toInt()
        )
        ViewCompat.postInvalidateOnAnimation(this@OriSmartDragLayout)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && enableDrag
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        //必须要取消，否则会导致滑动初次延迟
        scroller!!.abortAnimation()
    }

    override fun onStopNestedScroll(target: View) {
        finishScroll()
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        scrollTo(scrollX, scrollY + dyUnconsumed)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy > 0) {
            //scroll up
            val newY: Int = scrollY + dy
            if (newY < maxY) {
                consumed[1] = dy // dy不一定能消费完
            }
            scrollTo(scrollX, newY)
        }
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        val isDragging = scrollY in (minY + 1) until maxY
        if (isDragging && velocityY < -1500 && !isThreeDrag) {
            close()
        }
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun getNestedScrollAxes(): Int {
        return ViewCompat.SCROLL_AXIS_VERTICAL
    }

    fun isThreeDrag(isThreeDrag: Boolean) {
        this.isThreeDrag = isThreeDrag
    }

    fun enableDrag(enableDrag: Boolean) {
        this.enableDrag = enableDrag
    }

    fun setDuration(duration: Int) {
        this.duration = duration
    }

    fun dismissOnTouchOutside(dismissOnTouchOutside: Boolean) {
        this.dismissOnTouchOutside = dismissOnTouchOutside
    }

    fun setOnCloseListener(listener: OnCloseListener) {
        this.listener = listener
    }
}
