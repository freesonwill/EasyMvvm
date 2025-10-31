package com.walisport.module.live.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import arch.cayenne.lib.common.utils.ext.startFadeAnimVideo
import com.walisport.module.live.ui.widget.LiveMainLayoutInterceptTouch.LiveMainSlideDirection

/*
负责大小变化
 */
class LiveMainLayoutScale @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var fragmentMedia: FragmentContainerView
    private lateinit var llVideo: ConstraintLayout

    private lateinit var llText: LinearLayoutCompat
    private lateinit var restoreState: ImageView
    private lateinit var liveVideoTop: ImageView

    private lateinit var tvVideoVs: TextView

    private var mLiveMainGesture: LiveMainGestureListener? = null
    private var scaleXtoY = 1.0f // 宽高比例
    private val density = resources.displayMetrics.density
    private var initialVideoHeight: Float = 211f * density
    private var initMinVideoHeight: Float = 50f * density
    private var minVideoHeight: Float = 50f * density
    private var maxVideoHeight: Float = 211f * density
    private var initialVideoWidth: Float = 511f * density
    private var minVideoWidth: Float = 110f * density
    private var maxVideoWidth: Float = 511f * density
    private var isVerticalScroll = true

    private var animationUp: Boolean = false
    private var animationDow: Boolean = false
    private var isCollapsed = false // 标记是否处于折叠状态
    private var isDowScroll = true // 标记是否往下滑动
    override fun onFinishInflate() {
        super.onFinishInflate()
        // 初始化视图
        fragmentMedia = findViewById(R.id.fragment_media)
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
            fragmentMedia.pivotX = fragmentMedia.width.toFloat() / 2
            fragmentMedia.pivotY = 0f // 高度顶部
            // LogUtils.e("MainLayout------>onFinishInflate")
        }
    }

    fun initHeight(height:Int){
        llVideo.post {
            if (height>minVideoHeight){
            initialVideoHeight = height.toFloat()
            maxVideoHeight = height.toFloat()
            maxVideoWidth = llVideo.width.toFloat()
            initialVideoWidth = maxVideoWidth
            scaleXtoY = maxVideoWidth / initialVideoHeight // 计算宽高比例
            minVideoWidth = scaleXtoY * minVideoHeight // 最小宽度
            llVideo.layoutParams.width = maxVideoWidth.toInt()
            val paramsLin = llVideo.layoutParams as LayoutParams
            paramsLin.height = initialVideoHeight.toInt()
            llVideo.layoutParams = paramsLin
            fragmentMedia.layoutParams.height = initialVideoHeight.toInt()
            // 设置初始缩放中心（只设置宽度中心）
            fragmentMedia.pivotX = fragmentMedia.width.toFloat() / 2
            fragmentMedia.pivotY = 0f // 高度顶部
            // LogUtils.e("MainLayout------>onFinishInflate")
            }
        }
    }

    fun setCompetitionName(name: String) {
        tvVideoVs.setText(name)
    }

    fun setIsDowScroll(isDowScroll: Boolean) {
        isVerticalScroll = !isDowScroll
        this.isDowScroll = isDowScroll
    }
    fun isDirectionToScroll(): Boolean {
        val currentHeight = llVideo.layoutParams.height.toFloat()
        // 如果当前高度在 80-211 范围内，返回 true，表示可以滑动
        return currentHeight in (minVideoHeight+1)..maxVideoHeight
    }


    fun adjustLayout(deltaY: Float, direction: LiveMainSlideDirection) {

        if (isCollapsed) {//折叠状态后滑动
            llVideo.visibility = VISIBLE
            fragmentMedia.visibility = VISIBLE
            fragmentMedia.width.toFloat() / 2 // 宽度中心
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



        // 当高度达到最小值时触发隐藏动画
        if (newHeight <= minVideoHeight && llText.visibility == GONE) {
            if (!animationUp){
                animationUp = true
                animationDow = false
                fragmentMedia.startFadeAnimVideo {
                    llVideo.setBackgroundResource(R.color.menu_lin_tr)
                    fragmentMedia.pivotX = 0f
                    llText.visibility = VISIBLE
                    it.invoke()
                }
            }
        } else if (newHeight > minVideoHeight && llText.visibility == VISIBLE) {
            llText.visibility = GONE
            llVideo.setBackgroundResource(arch.cayenne.lib.common.R.color.tran_0)
            if (!animationDow){
                animationDow = true
                animationUp = false
                fragmentMedia.startFadeAnimVideo {
                    fragmentMedia.pivotX = fragmentMedia.width.toFloat() / 2 // 宽度中心
                    it.invoke()
                }
            }

        }
        // 不设置 pivotY，保持默认（顶部，pivotY = 0）

        // 应用等比缩放
        fragmentMedia.post {
            fragmentMedia.scaleX = targetScale
            fragmentMedia.scaleY = targetScale
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
        val currentScale = fragmentMedia.scaleX
        val targetScale = 1.0f // 初始缩放比例（对应 maxVideoHeight）
        llText.visibility = GONE
        // 设置缩放中心
        fragmentMedia.pivotY = 0f // 高度顶部
        fragmentMedia.pivotX = fragmentMedia.width.toFloat() / 2 // 宽度中心（初始状态非最小高度）

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
                fragmentMedia.scaleX = scale
                fragmentMedia.scaleY = scale
            }
            start()
        }
        llVideo.alpha = 1f
        // 更新初始值
        initialVideoHeight = targetHeight
        initialVideoWidth = targetHeight * scaleXtoY
    }

    /**
     * 将 fragmentVideo 的缩放比例和 llVideo 的高度设置为 0
     * @param duration 动画时长（毫秒）
     */
    fun collapseToZero(duration: Long = 300) {
        if (llVideo.layoutParams.height == 0 && fragmentMedia.scaleX == 0f) {
            // LogUtils.e("MainLayout----collapseToZero: already collapsed")
            return // 已经折叠，直接返回
        }

        // LogUtils.e("MainLayout----collapseToZero: start, currentHeight=${llVideo.layoutParams.height}, currentScale=${fragmentVideo.scaleX}")
        // 获取当前高度和缩放比例
        val currentHeight = llVideo.layoutParams.height.toFloat()
        val targetHeight = 0f // 目标高度 0
        val currentScale = fragmentMedia.scaleX
        val targetScale = 0f // 目标缩放比例 0

        // 设置缩放中心
        fragmentMedia.pivotY = 0f // 高度顶部
        fragmentMedia.pivotX = 0f // 宽度左边缘（与最小高度一致）

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
                    fragmentMedia.pivotX = fragmentMedia.width.toFloat() / 2 // 宽度中心
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
                fragmentMedia.scaleX = scale
                fragmentMedia.scaleY = scale
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    fragmentMedia.visibility = View.GONE // 动画结束时隐藏
                    llText.visibility = GONE
                    llVideo.alpha = 1f
                    LogUtils.e("MainLayout----collapseToZero: scale=0, visibility=GONE")
                }
            })
            start()
        }

        // 更新初始值
        initialVideoHeight = targetHeight
        initialVideoWidth = targetHeight * scaleXtoY
    }


    // 渐变显示动画
    fun View.fadeIn(duration: Long = 300, onAnimationEnd: (() -> Unit)? = null) {
        if ( alpha == 1f) return // 已经可见且完全不透明，直接返回
        // 确保 View 初始状态
        alpha = 0f
        animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd?.invoke() // 执行动画结束回调
                }
            })
            .start()
    }

    // 渐变隐藏动画
    fun View.fadeOut(duration: Long = 300, onAnimationEnd: (() -> Unit)? = null) {
        if ( alpha == 0f) return // 已经不可见或完全透明，直接返回

        animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    alpha = 1f // 重置 alpha 为下次动画做准备
                    onAnimationEnd?.invoke() // 执行动画结束回调
                }
            })
            .start()
    }


    fun setOnGestureListener(gestureListener: LiveMainGestureListener) {
        mLiveMainGesture = gestureListener
    }
}
