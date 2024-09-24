package com.cn.game.sdk2.ui.xpopup

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.cn.game.sdk2.R
import com.lxj.xpopup.enums.LayoutStatus
import kotlin.math.abs

class CustomDragLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : OriSmartDragLayout(context, attrs, defStyleAttr) {

    private var isHome: Boolean = false
    private var statusChangeListener: OnStatusChangeListener? = null

    private var tempX = 0f
    private var tempY = 0f
    private val scaledTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop

    interface OnStatusChangeListener: OnCloseListener {
        override fun onOpen()
        fun onOpening()
        override fun onClose()
        fun onClosing()
        override fun onDrag(y: Int, percent: Float, isScrollUp: Boolean)
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomDragLayout, 0, 0)
            .apply {
                try {
                    isHome = getBoolean(R.styleable.CustomDragLayout_isHome, false)
                } finally {
                    recycle()
                }
            }
    }

    // 設置是否為主遊戲畫面
    fun setIsHome(isHome: Boolean) {
        this.isHome = isHome
    }

    fun setOnStatusChangeListener(statusListener: OnStatusChangeListener) {
        statusChangeListener = statusListener
        listener = statusListener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 如果是主遊戲畫面須額外處理注區下滑、移動事件
        if(isHome) {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 紀錄初始位置
                    tempX = ev.x
                    tempY = ev.y
                    onTouchEvent(ev)
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = ev.x - tempX
                    val deltaY = ev.y - tempY

                    // 左右滑動不處理
                    // 攔截上下滑動
                    if (abs(deltaX) > abs(deltaY) && abs(deltaX) > scaledTouchSlop) {
                        return false
                    } else if (deltaY > scaledTouchSlop) {
                        onTouchEvent(ev)
                        return true
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun scrollTo(x: Int, y: Int) {
        var tempY = y
        if (tempY > maxY) { tempY = maxY }
        if (tempY < minY) { tempY = minY }

        val fraction = (tempY - minY).toFloat() / (maxY - minY).toFloat()

        isScrollUp = tempY > scrollY
        if (isUserClose && (fraction == 0.0f) && (this.status != LayoutStatus.Close)) {
            if (status == LayoutStatus.Open) statusChangeListener?.onClosing()
            status = LayoutStatus.Close
            listener?.onClose()
        } else if (fraction == 1.0f && status != LayoutStatus.Open) {
            if (status == LayoutStatus.Close) statusChangeListener?.onOpening()
            this.status = LayoutStatus.Open
            listener?.onOpen()
        }

        listener?.onDrag(tempY, fraction, isScrollUp)

        super.scrollTo(x, tempY)
    }

    override fun open() {
        post {
            val dy: Int = maxY - scrollY
            smoothScroll(if (enableDrag && isThreeDrag) dy / 3 else dy, true)
            status = LayoutStatus.Opening
            statusChangeListener?.onOpening()
        }
    }

    override fun close() {
        isUserClose = true
        post {
            scroller!!.abortAnimation()
            smoothScroll(minY - scrollY, false)
            status = LayoutStatus.Closing
            statusChangeListener?.onClosing()
        }
    }
}