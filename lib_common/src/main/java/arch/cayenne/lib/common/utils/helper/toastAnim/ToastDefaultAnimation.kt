package arch.cayenne.lib.common.utils.helper.toastAnim

import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

open class ToastDefaultAnimation: ToastAnimation {

    override val animDuration: Long
        get() = 150L
    override val showDuration: Long
        get() = 2_000L

    override fun getQueueTag(): String? {
        return this.javaClass.simpleName
    }

    override fun onBeforeAddView(view: View) {

    }

    override fun onAfterAddView(view: View) {

    }

    override fun getLayoutParams(view: View): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.CENTER
        return layoutParams
    }

    override suspend fun playShowAnim(view: View) {
        playAnim(view, true)
    }

    override suspend fun playDismissAnim(view: View) {
        playAnim(view, false)
    }

    private suspend fun playAnim(view: View, show: Boolean): Int {
        return suspendCancellableCoroutine { continuation ->
            view.apply {
                val start = if (show) 0.6f else 1f
                val end = if (!show) 0f else 1f
                val anim = ValueAnimator.ofFloat(start, end).apply {
                    duration = animDuration
                    addUpdateListener { animation ->
                        val p = animation.animatedValue as Float
                        scaleX = p
                        alpha = p
                        scaleY = p
                    }
                    var isCanceled = false
                    addListener(
                        onStart = {
                            scaleX = start
                            alpha = start
                            scaleY = start
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