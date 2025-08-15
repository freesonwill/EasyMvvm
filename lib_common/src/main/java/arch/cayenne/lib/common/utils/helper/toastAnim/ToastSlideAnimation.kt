package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ToastSlideAnimation(private val statusHeight: Int): ToastAnimation {
    override val showAnimDuration: Long
        get() = 200L
    override val showDuration: Long
        get() = 3_000L
    override val dismissAnimDuration: Long
        get() = showAnimDuration

    override fun getQueueTag(): String? {
        return this.javaClass.simpleName
    }

    override fun onBeforeAddView(view: View) {
        view.visibility = View.INVISIBLE
    }

    override fun onAfterAddView(view: View) {
        view.visibility = View.INVISIBLE
    }

    override fun getLayoutParams(view: View): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()

        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        layoutParams.height = view.measuredHeight + statusHeight
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = -statusHeight
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        return layoutParams
    }

    override suspend fun playShowAnim(view: View) {
        playSlideAnim(view, true)
    }

    override suspend fun playDismissAnim(view: View) {
        playSlideAnim(view, false)
    }

    private suspend fun playSlideAnim(view: View, show: Boolean): Int {
        return suspendCancellableCoroutine { continuation ->
            view.apply {
                val h = height
                val start = if (show) -(statusHeight + h).toFloat() else statusHeight.toFloat()
                val end = if (show) statusHeight.toFloat() else -(statusHeight + h).toFloat()
                val anim = ValueAnimator.ofFloat(start, end).apply {
                    duration = showAnimDuration
                    addUpdateListener { animation ->
                        translationY = animation.animatedValue as Float
                    }
                    var isCanceled = false
                    addListener(
                        onStart = {
                            if (show) view.isVisible = true
                            translationY = start
                        },
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
}