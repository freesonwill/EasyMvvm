package com.walisport.module.live.ui.widget

import android.animation.ValueAnimator
import com.walisport.module.live.R


import android.view.View

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.ui.widget.LiveMainLayoutInterceptTouch.LiveMainSlideDirection

/*
负责大小变化
 */
class LiveMainLayoutScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var fragmentVideo: FragmentContainerView
    private lateinit var llVideo: ConstraintLayout

    private lateinit var llText: LinearLayoutCompat
    private lateinit var restoreState: ImageView
    private lateinit var liveVideoTop: ImageView

    private lateinit var tvVideoVs: TextView

    private var mLiveMainGesture: LiveMainGestureListener? = null
    private var scaleXtoY = 1.0f // 宽高比例
    private val density = resources.displayMetrics.density
    private var initialVideoHeight: Float = 211f * density
    private var initMinVideoHeight: Float = 80f * density
    private var minVideoHeight: Float = 80f * density
    private var maxVideoHeight: Float = 211f * density
    private var initialVideoWidth: Float = 511f * density
    private var minVideoWidth: Float = 110f * density
    private var maxVideoWidth: Float = 511f * density
    private var isVerticalScroll = true

    private var isCollapsed = false // 标记是否处于折叠状态
    private var isDowScroll = true // 标记是否往下滑动
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        fragmentVideo = findViewById(R.id.fragment_video)
        llVideo = findViewById(R.id.llVideo)
        llText = findViewById(R.id.llText)
        tvVideoVs = findViewById(R.id.tv_video_vs)
        restoreState = findViewById(R.id.restoreToInitialState)
        liveVideoTop = findViewById(R.id.liveVideoTop)
        restoreState.clickNoRepeat {
            restoreToInitialState(200)
        }
        liveVideoTop.clickNoRepeat {
            collapseToZero(200)
        }
        // 设置初始高度和宽度
        llVideo.layoutParams.height = initialVideoHeight.toInt()
        llVideo.post {
            maxVideoWidth = llVideo.width.toFloat()
            initialVideoWidth = maxVideoWidth
            scaleXtoY = maxVideoWidth / initialVideoHeight // 计算宽高比例
            minVideoWidth = scaleXtoY * minVideoHeight // 最小宽度
            llVideo.layoutParams.width = maxVideoWidth.toInt()
            // 设置初始缩放中心（只设置宽度中心）
            fragmentVideo.pivotX = fragmentVideo.width.toFloat() / 2
            fragmentVideo.pivotY = 0f // 高度顶部
            // LogUtils.e("MainLayout------>onFinishInflate")
        }
    }

    fun setCompetitionName(name: String) {
        tvVideoVs.setText(name)
    }

    fun setIsDowScroll(isDowScroll: Boolean) {
        isVerticalScroll = !isDowScroll
        this.isDowScroll = isDowScroll
    }

    fun adjustLayout(deltaY: Float, direction: LiveMainSlideDirection) {
        if (isCollapsed) {//折叠状态后滑动
            llVideo.visibility = VISIBLE
            fragmentVideo.visibility = VISIBLE
            fragmentVideo.width.toFloat() / 2 // 宽度中心
        }

        LogUtils.e("MainLayout------>adjustLayout")
        // 获取当前高度
        val currentHeight = llVideo.layoutParams.height.toFloat()
        // 计算目标高度，限制在 minVideoHeight 和 maxVideoHeight 之间  如果是折叠状态,minVideoHeight没有大小限制
        val newHeight = (currentHeight + deltaY).coerceIn(minVideoHeight, maxVideoHeight)
        if (newHeight>=initMinVideoHeight){
            minVideoHeight = initMinVideoHeight
        }
        // 根据宽高比例计算目标宽度
        val newWidth = newHeight * scaleXtoY
        // 更新 llVideo 的高度
        val paramsLin = llVideo.layoutParams as LayoutParams
        paramsLin.height = newHeight.toInt()
        llVideo.layoutParams = paramsLin

        // 计算缩放比例（基于高度变化）
        val targetScale = newHeight / maxVideoHeight

        // 设置缩放中心：只设置宽度中心（X轴）
        // 当高度达到 minVideoHeight 时，pivotX 设置为 0，否则为宽度中心
            fragmentVideo.pivotX = if (newHeight <= minVideoHeight) {
                0f
            } else {
                fragmentVideo.width.toFloat() / 2 // 宽度中心
            }

        // 当高度达到最小值时触发隐藏动画
        if (newHeight <= minVideoHeight && llText.visibility == GONE) {
                llText.visibility = VISIBLE
        } else if (newHeight > minVideoHeight && llText.visibility == VISIBLE) {
            llText.visibility = GONE
        }
        // 不设置 pivotY，保持默认（顶部，pivotY = 0）

        // 应用等比缩放
        fragmentVideo.post {
            fragmentVideo.scaleX = targetScale
            fragmentVideo.scaleY = targetScale
        }

        // 更新初始值
        initialVideoHeight = newHeight
        initialVideoWidth = newWidth
        isCollapsed = false
    }

    /**
     * 渐变显示 fragmentVideo
     * @param duration 动画时长（毫秒）
     */
    fun showWithFade(view: View, duration: Long = 300) {
        ValueAnimator.ofFloat(view.alpha, 1f).apply {
            this.duration = duration
            addUpdateListener { animator ->
                view.alpha = animator.animatedValue as Float
            }
            start()
        }
    }

    /**
     * 渐变隐藏 fragmentVideo
     * @param duration 动画时长（毫秒）
     */
    fun hideWithFade(view: View, duration: Long = 300) {
        ValueAnimator.ofFloat(view.alpha, 0f).apply {
            this.duration = duration
            addUpdateListener { animator ->
                view.alpha = animator.animatedValue as Float
            }
            start()
        }
    }

    /**
     * 还原 fragmentVideo、clBottom 和 llVideo 的高度和缩放比例到初始状态
     * @param duration 动画时长（毫秒）
     */
    fun restoreToInitialState(duration: Long = 300) {
        LogUtils.e("MainLayout------>restoreToInitialState")
        // 获取当前高度和缩放比例
        val currentHeight = llVideo.layoutParams.height.toFloat()
        val targetHeight = maxVideoHeight // 目标高度（211dp）
        val currentScale = fragmentVideo.scaleX
        val targetScale = 1.0f // 初始缩放比例（对应 maxVideoHeight）
        llText.visibility = GONE
        // 设置缩放中心
        fragmentVideo.pivotY = 0f // 高度顶部
        fragmentVideo.pivotX = fragmentVideo.width.toFloat() / 2 // 宽度中心（初始状态非最小高度）

        // 动画还原 llVideo 高度
        ValueAnimator.ofFloat(currentHeight, targetHeight).apply {
            this.duration = duration
            addUpdateListener { animator ->
                val height = animator.animatedValue as Float
                val paramsLin = llVideo.layoutParams as LayoutParams
                paramsLin.height = height.toInt()
                llVideo.layoutParams = paramsLin
            }
            start()
        }

        // 动画还原 fragmentVideo 缩放比例
        ValueAnimator.ofFloat(currentScale, targetScale).apply {
            this.duration = duration
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                fragmentVideo.scaleX = scale
                fragmentVideo.scaleY = scale
            }
            start()
        }

        // 更新初始值
        initialVideoHeight = targetHeight
        initialVideoWidth = targetHeight * scaleXtoY
    }

    /**
     * 将 fragmentVideo 的缩放比例和 llVideo 的高度设置为 0
     * @param duration 动画时长（毫秒）
     */
    fun collapseToZero(duration: Long = 300) {
        if (llVideo.layoutParams.height == 0 && fragmentVideo.scaleX == 0f) {
            // LogUtils.e("MainLayout----collapseToZero: already collapsed")
            return // 已经折叠，直接返回
        }

        // LogUtils.e("MainLayout----collapseToZero: start, currentHeight=${llVideo.layoutParams.height}, currentScale=${fragmentVideo.scaleX}")
        // 获取当前高度和缩放比例
        val currentHeight = llVideo.layoutParams.height.toFloat()
        val targetHeight = 0f // 目标高度 0
        val currentScale = fragmentVideo.scaleX
        val targetScale = 0f // 目标缩放比例 0

        // 设置缩放中心
        fragmentVideo.pivotY = 0f // 高度顶部
        fragmentVideo.pivotX = 0f // 宽度左边缘（与最小高度一致）

        // 动画将 llVideo 高度设置为 0
        ValueAnimator.ofFloat(currentHeight, targetHeight).apply {
            this.duration = duration
            addUpdateListener { animator ->
                val height = animator.animatedValue as Float
                val paramsLin = llVideo.layoutParams as LayoutParams
                paramsLin.height = height.toInt()
                llVideo.layoutParams = paramsLin
                if (height.toInt() == 0) {
                    llVideo.visibility = GONE // 动画结束时隐藏
                }
                LogUtils.e("MainLayout----collapseToZero: height=${height.toInt()}")
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    llVideo.visibility = View.GONE // 动画结束时隐藏
                    isCollapsed = true // 标记折叠状态
                    minVideoHeight = 0f
                    fragmentVideo.width.toFloat() / 2 // 宽度中心
                    // LogUtils.e("MainLayout----collapseToZero: animation ended, height=0, visibility=GONE")
                }
            })
            start()
        }

        // 动画将 fragmentVideo 缩放比例设置为 0
        ValueAnimator.ofFloat(currentScale, targetScale).apply {
            this.duration = duration
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                fragmentVideo.scaleX = scale
                fragmentVideo.scaleY = scale
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    fragmentVideo.visibility = View.GONE // 动画结束时隐藏
                    llText.visibility = GONE
                    LogUtils.e("MainLayout----collapseToZero: scale=0, visibility=GONE")
                }
            })
            start()
        }

        // 更新初始值
        initialVideoHeight = targetHeight
        initialVideoWidth = targetHeight * scaleXtoY
    }

    fun setOnGestureListener(gestureListener: LiveMainGestureListener) {
        mLiveMainGesture = gestureListener
    }
}
