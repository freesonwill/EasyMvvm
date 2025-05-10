package arch.cayenne.lib.qyplayer.gesture

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 手势滑动的view。用于UI中处理手势的滑动事件，从而去实现手势改变亮度，音量，seek等操作。
 */
class GestureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mOutGestureListener: GestureListener? = null
    private var mIsFullScreenLocked = false

    init {
        GestureControl(context, this).apply {
            setView(this@GestureView)
            setOnGestureControlListener(object : GestureListener {
                override fun onHorizontalDistance(downX: Float, nowX: Float) {
                    // 其他手势如果锁住了就不回调
                    if (mIsFullScreenLocked) {
                        return
                    }
                    mOutGestureListener?.onHorizontalDistance(downX, nowX)
                }

                override fun onLeftVerticalDistance(downY: Float, nowY: Float) {
                    // 其他手势如果锁住了就不回调
                    if (mIsFullScreenLocked) {
                        return
                    }
                    mOutGestureListener?.onLeftVerticalDistance(downY, nowY)
                }

                override fun onRightVerticalDistance(downY: Float, nowY: Float) {
                    // 其他手势如果锁住了就不回调
                    if (mIsFullScreenLocked) {
                        return
                    }
                    mOutGestureListener?.onRightVerticalDistance(downY, nowY)
                }

                override fun onGestureEnd() {
                    // 其他手势如果锁住了就不回调
                    if (mIsFullScreenLocked) {
                        return
                    }
                    mOutGestureListener?.onGestureEnd()
                }

                override fun onSingleTap() {
                    mOutGestureListener?.onSingleTap()
                }

                override fun onDoubleTap() {
                    // 其他手势如果锁住了就不回调
                    if (mIsFullScreenLocked) {
                        return
                    }
                    mOutGestureListener?.onDoubleTap()
                }

                override fun onLongPress() {
                    // 其他手势如果锁住了就不回调
                    if (mIsFullScreenLocked) {
                        return
                    }
                    mOutGestureListener?.onLongPress()
                }
            })
        }
    }

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }

    fun setOnGestureListener(gestureListener: GestureListener) {
        mOutGestureListener = gestureListener
    }

    /**
     * 设置是否锁定全屏了。锁定全屏的话，除了单击手势有响应，其他都不会有响应。
     *
     * @param locked true：锁定。
     */
    fun setScreenLockStatus(locked: Boolean) {
        mIsFullScreenLocked = locked
    }
}

interface GestureListener {
    /**
     * 水平滑动距离
     *
     * @param downX 按下位置
     * @param nowX  当前位置
     */
    fun onHorizontalDistance(downX: Float, nowX: Float)

    /**
     * 左边垂直滑动距离
     *
     * @param downY 按下位置
     * @param nowY  当前位置
     */
    fun onLeftVerticalDistance(downY: Float, nowY: Float)

    /**
     * 右边垂直滑动距离
     *
     * @param downY 按下位置
     * @param nowY  当前位置
     */
    fun onRightVerticalDistance(downY: Float, nowY: Float)

    /**
     * 手势结束
     */
    fun onGestureEnd()

    /**
     * 单击事件
     */
    fun onSingleTap()

    /**
     * 双击事件
     */
    fun onDoubleTap()

    /**
     * 长按事件
     */
    fun onLongPress()
}