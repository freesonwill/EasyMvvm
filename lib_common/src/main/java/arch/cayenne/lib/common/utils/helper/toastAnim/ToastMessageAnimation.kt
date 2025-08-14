package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class ToastMessageAnimation: ToastDefaultAnimation() {

    private var lastTranslationY = 0
    private var animator: ValueAnimator? = null

    override fun getLayoutParams(view: View): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        layoutParams.gravity = Gravity.TOP
        layoutParams.y = getPositionY(view)
        return layoutParams
    }

    override fun isPlayQueueAnim(): Boolean {
        return true
    }

    override suspend fun playQueueAnim(view: View) {
        playSqueezeToTopAnimation(view)
    }

    private fun getPositionY(view: View): Int {
        val h = view.resources.displayMetrics.heightPixels
        return h / 2 - (view.measuredHeight / 2)
    }

    private fun playSqueezeToTopAnimation(v: View) {
        if (lastTranslationY < 0) return
        val windowManager = v.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = v.layoutParams as WindowManager.LayoutParams
        val height = (v.height + 8.dp2px)
        val targetPosition = if (lastTranslationY == layoutParams.y || lastTranslationY == 0) {
            layoutParams.y - height
        } else {
            layoutParams.y - height - (layoutParams.y - lastTranslationY)
        }
        lastTranslationY = targetPosition
        animator?.cancel()
        animator = ValueAnimator.ofInt(layoutParams.y, targetPosition).apply {
            duration = showAnimDuration
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                if (v.isAttachedToWindow) {
                    layoutParams.y = value
                    windowManager.updateViewLayout(v, layoutParams)
                } else {
                    this.cancel()
                }
            }
            start()
        }

    }
}