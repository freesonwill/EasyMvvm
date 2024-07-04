package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class ClickRecyclerView : RecyclerView {

    private var oldX = 0f
    private var oldY = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                oldX = event.x
                oldY = event.y
            }

            MotionEvent.ACTION_UP -> {
                val newX = event.x
                val newY = event.y
                if (abs(oldX - newX) < 5 && abs(oldY - newY) < 5) {
                    onRecyclerClickListener?.onRecyclerClick()
                }
            }
        }
        return true
    }

    private var onRecyclerClickListener: RecyclerClickListener? = null

    fun setOnRecycleClickListener(listener: RecyclerClickListener) {
        onRecyclerClickListener = listener
    }

    interface RecyclerClickListener {
        fun onRecyclerClick()
    }
}