package com.walisport.module.live.ui.widget
import com.walisport.module.live.R


import android.view.View

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentContainerView

class MainLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var fragmentVideo: FragmentContainerView
    private lateinit var skinTab: View
    private lateinit var clBottom: ConstraintLayout
    private var gestureDetector: GestureDetectorCompat? = null

    // 初始高度和范围（dp 转换为像素）
    private val density = resources.displayMetrics.density
    private var initialVideoHeight: Float = 211f * density
    private val minVideoHeight: Float = 50f * density
    private val maxVideoHeight: Float = 211f * density

    // 缩放范围
    private val minScale = 0.7f // 最小缩放比例（可调整）
    private val maxScale = 1.0f // 最大缩放比例

    // 滑动方向阈值（用于区分水平/垂直滑动）
    private var isVerticalScroll = false
    private var initialX = 0f
    private var initialY = 0f

    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        fragmentVideo = findViewById(R.id.fragment_video)
        skinTab = findViewById(R.id.skinTab)
        clBottom = findViewById(R.id.ClBotton)

        // 设置初始高度和缩放
        fragmentVideo.layoutParams.height = initialVideoHeight.toInt()
        fragmentVideo.scaleX = maxScale
        fragmentVideo.scaleY = maxScale
        // 设置缩放中心为视图中心
        fragmentVideo.pivotX = fragmentVideo.width / 2f
        fragmentVideo.pivotY = initialVideoHeight / 2f

        // 确保 skinTab 在 fragmentVideo 之上
        skinTab.elevation = 1f

        // 设置手势检测
        setupGestureDetector()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 确保 pivotX 在布局后更新（宽度可能在此时确定）
        fragmentVideo.pivotX = fragmentVideo.width / 2f
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (isVerticalScroll) {
                    adjustLayout(-(distanceY * 1.5f))
                    return true
                }
                return false
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.onInterceptTouchEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
                isVerticalScroll = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(ev.x - initialX)
                val dy = Math.abs(ev.y - initialY)
                if (dy > dx && dy > 50) { // 阈值50像素，优先垂直滑动
                    isVerticalScroll = true
                    return true // 拦截事件，处理垂直滑动
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    private fun adjustLayout(deltaY: Float) {
        val clampedDeltaY = deltaY.coerceIn(-50f, 50f) // 限制 deltaY 在合理范围内避免滑动跳动问题
        LogUtils.e("MainLayout----adjustLayout, deltaY: $deltaY, height: ${fragmentVideo.layoutParams.height}")

        // 获取当前高度
        val currentHeight = fragmentVideo.layoutParams.height.toFloat()
        // 计算目标高度
        val newHeight = (currentHeight + clampedDeltaY).coerceIn(minVideoHeight, maxVideoHeight)

        // 更新 fragment_video 高度
        val params = fragmentVideo.layoutParams as LayoutParams
        params.height = newHeight.toInt()
        fragmentVideo.layoutParams = params

        // 更新缩放中心（高度变化后，pivotY 需重新计算）
        fragmentVideo.pivotY = newHeight / 2f

        // 计算 fragmentVideo 的缩放比例
        val scale = (newHeight - minVideoHeight) / (maxVideoHeight - minVideoHeight) // 线性插值，范围 [0, 1]
        val targetScale = minScale + (maxScale - minScale) * scale

        // 应用等比缩放
        fragmentVideo.scaleX = targetScale
        fragmentVideo.scaleY = targetScale

        // 调整 skinTab 的 translationY 以匹配 fragmentVideo 缩放后的视觉底部
        val scaledHeight = newHeight * targetScale
        val visualBottomOffset = (newHeight - scaledHeight) / 2 // 缩放导致的底部偏移
        skinTab.translationY = -visualBottomOffset

        // 强制请求布局更新，确保 skinTab 和 clBottom 位置同步
        requestLayout()
        invalidate()
    }
}