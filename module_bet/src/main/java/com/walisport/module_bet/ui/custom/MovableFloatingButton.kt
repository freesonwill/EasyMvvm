package com.walisport.module_bet.ui.custom

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.window.layout.WindowMetricsCalculator
import com.walisport.module_bet.databinding.LayoutMovableFloatingButtonBinding
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
        when (event.action) {
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

                // 取得狀態欄 & 底部導航欄高度
                val statusBarHeight = getStatusBarHeight(context)
                val navigationBarHeight = getNavigationBarHeight(context)

                // 計算新的 X 座標 (限制在 0 ~ (parentWidth - vWidth))
                var newX = event.rawX + dX
                newX = 0f.coerceAtLeast(newX)
                newX = (parentWidth - vWidth).toFloat().coerceAtMost(newX)

                // 計算新的 Y 座標 (限制在 狀態欄底部 ~ 底部導航欄上方)
                var newY = event.rawY + dY
                newY = statusBarHeight.toFloat().coerceAtLeast(newY)
                newY = (parentHeight - navigationBarHeight - vHeight).toFloat().coerceAtMost(newY)

                // 設定位置
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
            MotionEvent.ACTION_CANCEL -> {
                downRawX = 0f
                downRawY = 0f
                dX = 0f
                dY = 0f
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    private fun getStatusBarHeight(context: Context): Int {
        val rect = Rect()
        val window = (context as? Activity)?.window
        window?.decorView?.getWindowVisibleDisplayFrame(rect)
        return rect.top
    }

    private fun getNavigationBarHeight(context: Context): Int {
        val metrics = context.resources.displayMetrics
        val usableHeight = metrics.heightPixels

        val realMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context).bounds
        val realHeight = realMetrics.height()

        return if (realHeight > usableHeight) realHeight - usableHeight else 0
    }

    override fun performClick(): Boolean {
        performClick?.invoke()
        super.performClick()
        return true
    }

    fun setPerformClick(performClick: () -> Unit) {
        this.performClick = performClick
    }

    fun setCount(count: Int) {
        binding.tvFloatPin.text = count.toString()
    }
}