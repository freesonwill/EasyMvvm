package com.walisport.module.live.ui.widget
import android.animation.ValueAnimator
import com.walisport.module.live.R


import android.view.View

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentContainerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import kotlin.math.abs
/*
上滑
滑到minVideoHeight 临界点后触发子类滑动 否则父类接收,不下发子类
下滑
子类 RecyclerView滑动到顶部,第一条,下发给父类,子类不接收
 */
class LiveMainLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private lateinit var fragmentVideo: FragmentContainerView
    private lateinit var llVideo: ConstraintLayout

    private lateinit var llText: LinearLayoutCompat
    private lateinit var restoreState: ImageView
    private lateinit var liveVideoTop: ImageView

    private lateinit var tvVideoVs: TextView

    private var mLiveMainGesture: LiveMainGestureListener? = null
    private var gestureDetector: GestureDetectorCompat? = null
    private var scaleXtoY = 1.0f // 宽高比例
    private val density = resources.displayMetrics.density
    private var initialVideoHeight: Float = 211f * density
    private var minVideoHeight: Float = 80f * density
    private var maxVideoHeight: Float = 211f * density
    private var initialVideoWidth: Float = 511f * density
    private var minVideoWidth: Float = 110f * density
    private var maxVideoWidth: Float = 511f * density
    private var isVerticalScroll = true
    private var initialX = 0f
    private var initialY = 0f
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
        restoreState.clickNoRepeat{
            restoreToInitialState(200)
        }
        liveVideoTop.clickNoRepeat{
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
        // 设置手势检测
        setupGestureDetector()
    }

     fun setCompetitionName(name: String){
         tvVideoVs.setText(name)
     }

    fun setIsDowScroll(isDowScroll: Boolean){
        isVerticalScroll = !isDowScroll
        this.isDowScroll = isDowScroll
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
                    adjustLayout(-distanceY * 4.0f) // 调整滑动灵敏度 跟手速度
                    return true
                }
                return false
            }
        })
    }

    private var isMove : Boolean = false
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.onInterceptTouchEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = ev.x
                initialY = ev.y
                isVerticalScroll = false
            }
            MotionEvent.ACTION_MOVE -> {
                isMove = true
                val dx = abs(ev.x - initialX)
                val dy = abs(ev.y - initialY)
                val deltaY = ev.y - initialY // 不取绝对值，以便判断方向
                if (dy > dx && dy > 5) { // 阈值10像素，优先垂直滑动
                        var currentHeight = llVideo.layoutParams.height.toFloat()
                   // LogUtils.e("MainLayout---onInterceptTouchEvent------deltaY${deltaY}")
                    if (deltaY > 0f) {// Y 增大，表示向下滑动,不处理往下滑动
                        isVerticalScroll = isDowScroll
                        if (isDowScroll){
                            isVerticalScroll = true
                        }
                        return isDowScroll
                    }else{// Y 减小，表示向上滑动
                        if (!isCollapsed){//如果折叠不处理上拉
                        LogUtils.e("MainLayout---onInterceptTouchEvent------currentHeight--${currentHeight}---minVideoHeight${minVideoHeight}")
                        if (currentHeight<=minVideoHeight){//上滑滑到小于最小值,可传给子类
                            isVerticalScroll = false //不处理缩小
                            LogUtils.e("MainLayout---onInterceptTouchEvent------滑到小于最小值--${currentHeight}")
                            mLiveMainGesture?.onRvVerticalScroll(true)//通知子类,可往上滑动
                            return false //false 不拦截
                        }
                        mLiveMainGesture?.onRvVerticalScroll(false)//不可上滑
                        isVerticalScroll = true //不处理缩小
                        }else{
                            mLiveMainGesture?.onRvVerticalScroll(true)//通知子类,可往上滑动
                            return false
                        }
                        return true
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector?.onTouchEvent(event) ?: super.onTouchEvent(event)
    }

    private fun adjustLayout(deltaY: Float) {
        if (isCollapsed){//折叠状态后滑动
            llVideo.visibility = VISIBLE
            fragmentVideo.visibility = VISIBLE
            isCollapsed = false
        }
        LogUtils.e("MainLayout------>adjustLayout")
        val clampedDeltaY = deltaY.coerceIn(-25f, 25f) // 限制 deltaY 在合理范围内避免滑动跳动问题  设置跟手速度需要比数值调大
        // 获取当前高度
        val currentHeight = llVideo.layoutParams.height.toFloat()
        // 计算目标高度，限制在 minVideoHeight 和 maxVideoHeight 之间
        val newHeight = (currentHeight + clampedDeltaY).coerceIn((if(isCollapsed)0f else minVideoHeight), maxVideoHeight)
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
        if (newHeight <= minVideoHeight && llText.visibility==GONE) {
            llText.visibility = VISIBLE
            mLiveMainGesture?.onRvVerticalScroll(true)//通知子类,可往上滑动
        } else if (newHeight > minVideoHeight && llText.visibility==VISIBLE) {
            llText.visibility = GONE
        }
        //到最大值,通知rv内U滑动
//        if (newHeight >= maxVideoHeight){
//            mLiveMainGesture?.onRvVerticalScroll(false)//通知子类,可往上滑动
//        }
//        if (newHeight <= minVideoHeight) {
//            fragmentVideo.alpha = 0f
//            showWithFade(llText,300)
//            showWithFade(fragmentVideo,300)
//            fragmentVideo.pivotX = 0f
//        } else {
//            fragmentVideo.alpha = 0f
//            hideWithFade(llText,300)
//            showWithFade(fragmentVideo,300)
//            fragmentVideo.width.toFloat() / 2 // 宽度中心
//        }

        // 不设置 pivotY，保持默认（顶部，pivotY = 0）

        // 应用等比缩放
        fragmentVideo.post {
            fragmentVideo.scaleX = targetScale
            fragmentVideo.scaleY = targetScale
        }

        // 更新初始值
        initialVideoHeight = newHeight
        initialVideoWidth = newWidth
        if(isCollapsed) isCollapsed = false//往下滑动后 设置不是折叠状态
    }

    /**
     * 渐变显示 fragmentVideo
     * @param duration 动画时长（毫秒）
     */
    fun showWithFade(view:View,duration: Long = 300) {
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
    fun hideWithFade(view:View,duration: Long = 300) {
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
                if ( height.toInt()==0){
                    llVideo.visibility = GONE // 动画结束时隐藏
                }
                LogUtils.e("MainLayout----collapseToZero: height=${height.toInt()}")
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    llVideo.visibility = View.GONE // 动画结束时隐藏
                    isCollapsed = true // 标记折叠状态
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

interface LiveMainGestureListener {
    /**
     * 子类是否接收滑动
     */
    fun onRvVerticalScroll(boolean: Boolean)
}