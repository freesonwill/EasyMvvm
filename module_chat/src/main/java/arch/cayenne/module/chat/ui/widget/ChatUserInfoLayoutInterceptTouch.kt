package com.walisport.module.live.ui.widget

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.LinearLayoutCompat
import com.walisport.module.live.ui.widget.ChatUserInfoLayoutInterceptTouch.ChatInfoSlideDirection
import kotlin.math.abs


/*
负责事件处理
 */
class ChatUserInfoLayoutInterceptTouch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private var mLiveMainGesture: ChatInfoGestureListener? = null
    private var lastX = 0f // 记录触摸起点的 X 坐标
    private var lastY = 0f // 记录触摸起点的 Y 坐标
    private var isDirectionDetermined = false // 是否已确定滑动方向
    private var determined = true // 是否是第一次滑动
    private var startTime = 0L // 记录滑动时间
    private val scrollSpeed = 3.0f //滑动速度
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    enum class ChatInfoSlideDirection {
        UP,     // 上滑
        DOWN,   // 下滑
        LEFT,   // 左滑
        RIGHT   // 右滑
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev ?: return super.onInterceptTouchEvent(ev)
        // 如果需要拦截触摸事件以确保手势处理，可以根据条件返回 true
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.y // 记录触摸起点
                lastX = event.x
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
                // LogUtils.e("MainLayout----------Sliding DOWN, pixelsY=-------------${abs((absDeltaY - lastAbsDeltaY))}")
                //处理在滑动viewpager2的时候,不触发视频播放区域的放大缩小
                if (!isDirectionDetermined) {
                    // 垂直滑动
                    if (deltaY > 0) {
                        mLiveMainGesture?.onAdjustLayoutScroll(
                            absDeltaY * scrollSpeed,
                            ChatInfoSlideDirection.DOWN
                        )
                    } else if (deltaY < 0) {
                        mLiveMainGesture?.onAdjustLayoutScroll(
                            -absDeltaY * scrollSpeed,
                            ChatInfoSlideDirection.UP
                        )
                    }
                }
                lastX = event.x
                lastY = event.y
            }


            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDirectionDetermined = false // 重置滑动方向
                determined = true
            }
        }
        return true
    }

    fun seGestureListener(liveMainGesture: ChatInfoGestureListener) {
        this.mLiveMainGesture = liveMainGesture
    }
}

interface ChatInfoGestureListener {
    /**
     * 子类是否接收滑动
     */
    fun onAdjustLayoutScroll(deltaY: Float, direction: ChatInfoSlideDirection)
}