package com.walisport.app.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.walisport.app.databinding.LayoutMovableFloatingButtonBinding
import kotlin.math.abs

class MovableFloatingButton : LinearLayout, View.OnTouchListener {

    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f
    private val binding: LayoutMovableFloatingButtonBinding
    private var performClick: (() -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = LayoutMovableFloatingButtonBinding.inflate(layoutInflater, this, true)
        setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                downRawX = event.rawX
                downRawY = event.rawY
                dX = v.x - downRawX
                dY = v.y - downRawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val vWidth = v.width
                val vHeight = v.height

                val viewParent = v.parent as View
                val parentWidth = viewParent.width
                val parentHeight = viewParent.height

                var newX = event.rawX + dX
                newX = 0f.coerceAtLeast(newX)
                newX = (parentWidth - vWidth).toFloat().coerceAtMost(newX)

                var newY = event.rawY + dY
                newY = 0f.coerceAtLeast(newY)
                newY = (parentHeight - vHeight).toFloat().coerceAtMost(newY)

                v.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start()

                return true
            }
            MotionEvent.ACTION_UP -> {
                val upRawX = event.rawX
                val upRawY = event.rawY

                val upDx = upRawX - downRawX
                val upDy = upRawY - downRawY
                if (abs(upDx) < 10 && abs(upDy) < 10) {
                    performClick()
                }
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        performClick?.invoke()
        return true
    }

    fun setPerformClick(performClick: () -> Unit) {
        this.performClick = performClick
    }

    fun setCount(count: Int) {
        binding.tvFloatPin.text = count.toString()
    }
}