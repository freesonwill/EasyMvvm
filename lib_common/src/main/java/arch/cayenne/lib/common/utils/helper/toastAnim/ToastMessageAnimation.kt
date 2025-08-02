package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ToastMessageAnimation: ToastDefaultAnimation() {

    private var lastTranslationY = 0

    override fun getLayoutParams(view: View): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

    private suspend fun playSqueezeToTopAnimation(v: View): Int {
        val windowManager = v.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return suspendCancellableCoroutine { continuation ->
            val layoutParams = v.layoutParams as WindowManager.LayoutParams
            val height = (v.height + 8.dp2px)
            val targetPosition = layoutParams.y - height
            lastTranslationY = targetPosition
            val anim = ValueAnimator.ofInt(layoutParams.y, lastTranslationY).apply {
                duration = animDuration
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    layoutParams.y = value
                    windowManager.updateViewLayout(v, layoutParams)
                }
                var isCanceled = false
                addListener(
                    onCancel = {
                        isCanceled = true
                        continuation.resume(-1)
                    },
                    onEnd = {
                        if (isCanceled) return@addListener
                        continuation.resume(200)
                    }
                )
                start()
            }
            continuation.invokeOnCancellation {
                if (anim.isRunning) anim.cancel()
            }

        }
    }
}