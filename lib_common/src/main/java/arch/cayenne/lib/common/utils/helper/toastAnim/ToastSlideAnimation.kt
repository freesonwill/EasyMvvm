package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ToastSlideAnimation: ToastAnimation {
    override val animDuration: Long
        get() = 200L
    override val showDuration: Long
        get() = 3_000L

    override fun onBeforeAddView(view: View) {
        view.isVisible = false
    }

    override fun onAfterAddView(view: View) {
        view.isVisible = false
    }

    override fun getLayoutParams(): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = 0 // 貼齊頂部
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
                val start = if (show) -height.toFloat() else 0f
                val end = if (show) 0f else -height.toFloat()
                val anim = ValueAnimator.ofFloat(start, end).apply {
                    duration = animDuration
                    addUpdateListener { animation ->
                        translationY = animation.animatedValue as Float
                    }
                    var isCanceled = false
                    addListener(
                        onStart = {
                            if (show) {
                                view.isVisible = true
                            }
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