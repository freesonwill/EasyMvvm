package com.walisport.module.live.ui.widget

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.LinearLayoutCompat
import com.walisport.module.live.ui.widget.LiveMainLayoutInterceptTouch.LiveMainSlideDirection
import kotlin.math.abs


/*
负责事件处理
 */
class LiveMainLayoutInterceptTouch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private var mLiveMainGesture: LiveMainGestureListener? = null
    private val density = resources.displayMetrics.density
    private var lastX = 0f // 记录触摸起点的 X 坐标
    private var lastY = 0f // 记录触摸起点的 Y 坐标
    private var startX = 0f
    private var startY = 0f
    private var isDirectionDetermined = false // 是否已确定滑动方向
    private var determined = true // 是否是第一次滑动
    private var startTime = 0L // 记录滑动时间
    private val scrollSpeed = 1.0f //滑动速度

    private val quickScrollTime: Long = 500
    private val quickScrollY: Float = 20f * density


    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    enum class LiveMainSlideDirection {
        UP,     // 上滑
        DOWN,   // 下滑
        QUICK_DOWN,   // 快速下滑
        QUICK_UP   // 快速上滑
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.onInterceptTouchEvent(ev)
       // LogUtils.e("MainLayout----------onInterceptTouchEvent")
        // 如果需要拦截触摸事件以确保手势处理，可以根据条件返回 true
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
       // LogUtils.e("MainLayout----------onTouchEvent--${event.y}")
      //  LogUtils.e("MainLayout----------onTouchEvent: action=${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.y
                lastX = event.x
                startY = event.y
                startX = event.x
                isDirectionDetermined = false // 重置滑动方向
                determined = true
                startTime = event.eventTime
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - lastX
                val deltaY = event.y - lastY
                val absDeltaX = abs(deltaX)
                val absDeltaY = abs(deltaY)

                //只判断第一次滑动,是否是触发view pager2 滑动
                if (determined) {
                    determined = false
                    if (abs(deltaX) > abs(deltaY)) {
                        // 水平滑动距离更大
                        isDirectionDetermined = true
                    } else {
                        // 垂直滑动距离更大
                        isDirectionDetermined = false
                    }
                }
                LogUtils.e("MainLayout----------Sliding DOWN, pixelsY=-------------${abs((absDeltaY))}")
                //处理在滑动viewpager2的时候,不触发视频播放区域的放大缩小
                if (!isDirectionDetermined) {
                    // 垂直滑动
                    if (deltaY > 0) {
                   //     LogUtils.e("MainLayout----------Sliding DOWN, pixelsY=$absDeltaY")
                        mLiveMainGesture?.onAdjustLayoutScroll(
                            absDeltaY * scrollSpeed,
                            LiveMainSlideDirection.DOWN
                        )
                    } else if (deltaY < 0) {
                   //     LogUtils.e("MainLayout----------Sliding UP, pixelsY=$absDeltaY")
                        mLiveMainGesture?.onAdjustLayoutScroll(
                            -absDeltaY * scrollSpeed,
                            LiveMainSlideDirection.UP
                        )
                    }
                }
                lastX = event.x
                lastY = event.y
            }


            MotionEvent.ACTION_UP -> {
              //  LogUtils.e("MainLayout----------Touch ended")
                isDirectionDetermined = false // 重置滑动方向
                determined = true
                val time = event.eventTime
                val deltaY = event.y - startY
                val absDeltaY = abs(deltaY)
                LogUtils.e("quickScrollY------>deltaY${deltaY},absDeltaY${absDeltaY},quickScrollY${quickScrollY},time${abs(time-startTime)},quickScrollTime${quickScrollTime}")
                //为快速滑动
                if (absDeltaY>=quickScrollY&&abs(time-startTime)<=quickScrollTime){
                    if (deltaY > 0) {
                        mLiveMainGesture?.onQuickAdjustLayoutScroll(
                            absDeltaY * scrollSpeed,
                            LiveMainSlideDirection.QUICK_DOWN
                        )
                    } else if (deltaY < 0) {
                        mLiveMainGesture?.onQuickAdjustLayoutScroll(
                            -absDeltaY * scrollSpeed,
                            LiveMainSlideDirection.QUICK_UP
                        )
                    }
                }
            }
        }
        return true
    }

    fun setLiveMainGestureListener(liveMainGesture: LiveMainGestureListener) {
        this.mLiveMainGesture = liveMainGesture
    }
}

interface LiveMainGestureListener {
    /**
     * 子类是否接收滑动
     */
    fun onAdjustLayoutScroll(deltaY: Float, direction: LiveMainSlideDirection)
    fun onQuickAdjustLayoutScroll(deltaY: Float, direction: LiveMainSlideDirection)



}