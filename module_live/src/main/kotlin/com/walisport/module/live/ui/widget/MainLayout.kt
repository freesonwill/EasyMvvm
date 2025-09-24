package com.walisport.module.live.ui.widget
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentContainerView
import arch.cayenne.lib.base.utils.LogUtils
import com.walisport.module.live.R
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

        // 设置初始高度
        fragmentVideo.layoutParams.height = initialVideoHeight.toInt()

        // 设置手势检测
        setupGestureDetector()
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
                    adjustLayoutHeight(-distanceY)
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
                if (dy > dx && dy > 20) { // 阈值20像素，优先垂直滑动
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
    //左右可以使用内边距实现
    private fun adjustLayoutHeight(deltaY: Float) {
        val clampedDeltaY = deltaY.coerceIn(-20f, 20f) // 限制 deltaY 在合理范围内避免滑动跳动问题
        LogUtils.e("MainLayout----adjustLayoutHeight,${deltaY},---height,${fragmentVideo.layoutParams.height}")
        // 获取当前 fragment_video 的高度
        val currentHeight = fragmentVideo.layoutParams.height.toFloat()
        // 计算新高度
        var newHeight = (currentHeight + clampedDeltaY).coerceIn(minVideoHeight, maxVideoHeight)

        // 更新 fragment_video 高度
        val params = fragmentVideo.layoutParams as LayoutParams
        params.height = newHeight.toInt()
        fragmentVideo.layoutParams = params
        // ClBotton 自适应剩余空间（通过 ConstraintLayout 约束自动处理）
        // 请求重新布局
    }
}