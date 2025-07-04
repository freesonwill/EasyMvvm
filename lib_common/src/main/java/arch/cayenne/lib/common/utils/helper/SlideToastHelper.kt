package arch.cayenne.lib.common.utils.helper

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resume

internal class SlideToastHelper private constructor() {

    companion object {
        const val DEFAULT_DURATION = 2_000L
        val instance: SlideToastHelper by lazy { SlideToastHelper() }
    }

    private var toastJob: Job? = null

    //方便cancelToast
    private var viewHolder: WeakReference<View>? = null
    private var view: View?
        get() = viewHolder?.get()
        set(value) {
            viewHolder = if (value == null) null else WeakReference(value)
        }

    /***
     * 自定義toast
     * @param view 需先自行實作view
     */
    fun showView(view: View, duration: Long = DEFAULT_DURATION) {
        Log.d("abcd", "++++ ")
        cancelToast(view.context)
        if (toastJob != null) {
            return
        }
        showToast(view, duration)
    }

    /**
     * 取消Toast
     */
    private fun cancelToast(context: Context) {
        toastJob?.cancel().let { toastJob = null }
        view?.let {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (it.isAttachedToWindow) {
                wm.removeView(it)
            }
            view = null
        }
    }

    private fun showToast(view: View, duration: Long) {
        this.view = view
        val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()

        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = 0 // 貼齊頂部
        wm.addView(view, layoutParams)

        // 先讓 view 在螢幕外
        view.translationY = -view.measuredHeight.toFloat()

        toastJob = CoroutineScope(Dispatchers.Main).launch {
            playSlideAnim(view, true)
            delay(duration)
            playSlideAnim(view, false)
            cancelToast(view.context)
        }
    }

    private suspend fun playSlideAnim(view: View, show: Boolean): Int {
        return suspendCancellableCoroutine { continuation ->
            view.apply {
                val start = if (show) -height.toFloat() else 0f
                val end = if (show) 0f else -height.toFloat()
                val anim = ValueAnimator.ofFloat(start, end).apply {
                    duration = 200
                    addUpdateListener { animation ->
                        translationY = animation.animatedValue as Float
                    }
                    var isCanceled = false
                    addListener(
                        onStart = {
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

fun Activity.showSlideToast(view: View, duration: Long = SlideToastHelper.DEFAULT_DURATION) {
    SlideToastHelper.instance.showView(view, duration)
}