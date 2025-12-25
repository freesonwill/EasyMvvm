package com.walisport.module.live.ui.widget

import arch.cayenne.lib.base.utils.LogUtils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.LinearLayoutCompat
import com.walisport.module.live.ui.widget.MeLayoutInterceptTouch.MeSlideDirection
import kotlin.math.abs


/*
负责事件处理
 */
class MeLayoutInterceptTouch @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val density = resources.displayMetrics.density
    private var mLiveMainGesture: ChatInfoGestureListener? = null
    private var lastX = 0f // 记录触摸起点的 X 坐标
    private var lastY = 0f // 记录触摸起点的 Y 坐标
    private var startY = 0f
    private var pointY = 5f * density

    private var isDirectionDetermined = false // 是否已确定滑动方向
    private var determined = true // 是否是第一次滑动
    private var startTime = 0L // 记录滑动时间
    private val scrollSpeed = 1.0f //滑动速度
    private var currDirection: MeSlideDirection = MeSlideDirection.UP
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    enum class MeSlideDirection {
        UP,     // 上滑
        DOWN   // 下滑
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
                startY = event.y
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
                //处理在滑动viewpager2的时候,不触发顶部区域放大缩小
                if (!isDirectionDetermined) {
                    // 垂直滑动
                    if (deltaY > 0) {
                        //如果不是相同方向滑动,需要判断滑动区域临界点,避免上下滑动跳动情况
                        if (currDirection != MeSlideDirection.DOWN) {
                            if (absDeltaY >= pointY) {
                                mLiveMainGesture?.onAdjustLayoutScroll(
                                    absDeltaY * scrollSpeed,
                                    MeSlideDirection.DOWN
                                )
                            }
                        } else {
                            mLiveMainGesture?.onAdjustLayoutScroll(
                                absDeltaY * scrollSpeed,
                                MeSlideDirection.DOWN
                            )
                        }
                        currDirection = MeSlideDirection.DOWN
                    } else if (deltaY < 0) {
                        if (currDirection != MeSlideDirection.UP) {
                            if (absDeltaY >= pointY) {
                                mLiveMainGesture?.onAdjustLayoutScroll(
                                    -absDeltaY * scrollSpeed,
                                    MeSlideDirection.UP
                                )
                            }
                        } else {
                            mLiveMainGesture?.onAdjustLayoutScroll(
                                -absDeltaY * scrollSpeed,
                                MeSlideDirection.UP
                            )
                        }
                        currDirection = MeSlideDirection.UP
                    }
                }
                lastX = event.x
                lastY = event.y
            }

            MotionEvent.ACTION_UP -> {
                isDirectionDetermined = false // 重置滑动方向
                determined = true
                val deltaY = event.y - startY
                if (deltaY > 0) {
                    mLiveMainGesture?.onAdjustLayoutScrollUp(
                        MeSlideDirection.DOWN
                    )
                } else {
                    mLiveMainGesture?.onAdjustLayoutScrollUp(
                        MeSlideDirection.UP
                    )
                }
            }
        }
        return true
    }

    fun seGestureListener(liveMainGesture: ChatInfoGestureListener) {
        this.mLiveMainGesture = liveMainGesture
    }
}

interface ChatInfoGestureListener {
    //正常滑动
    fun onAdjustLayoutScroll(deltaY: Float, direction: MeSlideDirection)
    //滑动抬起事件
    fun onAdjustLayoutScrollUp(direction: MeSlideDirection)
}