package arch.cayenne.lib.qyplayer.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import arch.cayenne.lib.qyplayer.util.ScreenUtils
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class GestureControl(context: Context, private val gestureView: View) {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var startTime: Long = 0L
    private val SWIPE_THRESHOLD = 140 // X轴滑动距离阈值
    private val SWIPE_MAX_TIME = 1600 // 时间间隔
    private val MAX_ANGLE_DEGREES = 45 // 最大允许角度
    //是否水平
    private var isInHorizontalGesture = false

    //是否右边垂直
    private var isInRightGesture = false

    //是否左边垂直
    private var isInLeftGesture = false

    private var mView: View? = null
    private var mGestureListener: GestureListener? = null

    private val mGestureDetector = GestureDetector(context, object : OnGestureListener {
        private var mXDown: Float = 0f

        override fun onDown(e: MotionEvent): Boolean {
            mXDown = e.x
            startX = e.x
            startY = e.y
            startTime = e.eventTime
            return true
        }

        override fun onShowPress(e: MotionEvent) {
            // noop
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (e1 == null) {
                return false
            }

            if (abs(distanceX) > abs(distanceY)) {
                if (isInLeftGesture || isInRightGesture) {
                    // 此前已经是竖直滑动了，不管
                } else {
                    isInHorizontalGesture = true
                }
            } else {
                // 垂直滑动
            }

            if (isInHorizontalGesture) {
                mGestureListener?.onHorizontalDistance(e1.x, e2.x)
            } else {
                if (ScreenUtils.isInLeft(context, mXDown.toInt())) {
                    isInLeftGesture = true
                    mGestureListener?.onLeftVerticalDistance(e1.y, e2.y)
                } else if (ScreenUtils.isInRight(context, mXDown.toInt())) {
                    isInRightGesture = true
                    mGestureListener?.onRightVerticalDistance(e1.y, e2.y)
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            mGestureListener?.onLongPress()
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

    })

    init {
        gestureView.setOnTouchListener { _, event ->
            when (event.action) {
                // 对结束事件的监听
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isInLeftGesture = false
                    isInRightGesture = false
                    isInHorizontalGesture = false

                    val endX = event.x
                    val endY = event.y
                    val endTime = event.eventTime
                    val diffX =abs(endX - startX)
                    val diffY = abs(endY - startY)
                    val timeDiff = endTime - startTime

                    // 计算滑动角度（弧度转角度）
                    val angle = Math.toDegrees(kotlin.math.atan2(diffY.toDouble(), diffX.toDouble())).toFloat()
                    // LogUtils.e("OnSwipeTouchListener: diffX=$diffX, diffY=$diffY, angle=$angle, timeDiff=$timeDiff")

                    startTime = 0L
                    // 判断：X轴滑动距离足够，时间符合，角度小于阈值
                    if (startX < endX && diffX > SWIPE_THRESHOLD && timeDiff <= SWIPE_MAX_TIME && angle < MAX_ANGLE_DEGREES) {
                        mGestureListener?.onBack()
                    }
                    mGestureListener?.onGestureEnd()
                }

                else -> {
                    // noop
                }
            }
            // 其他的事件交给GestureDetector
            return@setOnTouchListener mGestureDetector.onTouchEvent(event)
        }

        mGestureDetector.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                mGestureListener?.onSingleTap()
                return false
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                mGestureListener?.onDoubleTap()
                return false
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                return false
            }
        })
    }

    fun setView(view: View) {
        mView = view
    }

    /**
     * 设置手势监听事件
     */
    fun setOnGestureControlListener(gestureListener: GestureListener) {
        mGestureListener = gestureListener
    }
}