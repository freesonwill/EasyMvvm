package com.cn.game.sdk2.ui.xpopup

import android.content.Context
import android.util.AttributeSet
import com.lxj.xpopup.enums.LayoutStatus

class CustomDragLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : OriSmartDragLayout(context, attrs, defStyleAttr) {

    private var statusChangeListener: OnStatusChangeListener? = null
    interface OnStatusChangeListener: OnCloseListener {
        override fun onOpen()
        fun onOpening()
        override fun onClose()
        fun onClosing()
        override fun onDrag(y: Int, percent: Float, isScrollUp: Boolean)
    }

    fun setOnStatusChangeListener(statusListener: OnStatusChangeListener) {
        statusChangeListener = statusListener
        listener = statusListener
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