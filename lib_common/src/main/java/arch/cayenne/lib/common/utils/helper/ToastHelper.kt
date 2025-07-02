package arch.cayenne.lib.common.utils.helper

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import arch.cayenne.lib.common.databinding.ToastLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resume



internal class ToastHelper private constructor() {
    companion object {
        private const val TAG = "ToastHelper"
        const val DEFAULT_DURATION = 2_000L
        val instance: ToastHelper by lazy { ToastHelper() }
    }
    private var toastJob: Job? = null
    //方便cancelToast
    private var viewHolder:WeakReference<View>? = null
    private var view:View?
        get() = viewHolder?.get()
        set(value) { viewHolder = if(value == null) null else WeakReference(value) }

    // 新增 blockToast 機制
    private var blockToast: Boolean = false
    fun setBlockToast(block: Boolean) {
        blockToast = block
    }
    fun isBlockToast(): Boolean = blockToast

    /***
     * 預設toast
     * @param context
     * @param msg
     */
    fun showDefaultToast(context: Context, msg: String?, duration: Long = DEFAULT_DURATION) {
        if (blockToast) return
        cancelToast(context)
        if (toastJob != null) {
            return
        }
        val inflater = LayoutInflater.from(context)
        val layout = ToastLayoutBinding.inflate(inflater, null, false)

        layout.toastText.text = msg
        showToast(layout.root, duration)
    }

    /***
     * 自定義toast
     * @param view 需先自行實作view
     */
    fun showCustomToast(view: View, duration: Long = DEFAULT_DURATION) {
        if (blockToast) return
        cancelToast(view.context)
        if (toastJob != null) {
            return
        }
        showToast(view, duration)
    }

    /**
     * 取消Toast
     */
    fun cancelToast(context: Context){
        toastJob?.cancel().let { toastJob = null }
        view?.let {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if(it.isAttachedToWindow){
                wm.removeView(it)
            }
            view = null
        }
    }

    private fun showToast(view: View, duration: Long) {
        if (blockToast) return
        this.view = view
        val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()

        // 设置参数
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        layoutParams.gravity = Gravity.CENTER
        wm.addView(view, layoutParams)

        toastJob = CoroutineScope(Dispatchers.Main).launch {
            playAnim(view, true)
            delay(duration)
            playAnim(view, false)
            cancelToast(view.context)
        }
    }

    private suspend fun playAnim(view: View, show: Boolean): Int {
        return suspendCancellableCoroutine { continuation ->
            view.apply {
                val start = if (show) 0f else 1f
                val end = if (!show) 0f else 1f
                val anim = ValueAnimator.ofFloat(start, end).apply {
                    duration = 150
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

fun Fragment.showToast(msg: String?, duration: Long = ToastHelper.DEFAULT_DURATION) {
    ToastHelper.instance.showDefaultToast(requireContext(), msg, duration)
}

fun Fragment.showToast(view: View, duration: Long = ToastHelper.DEFAULT_DURATION) {
    ToastHelper.instance.showCustomToast(view, duration)
}

fun Activity.showToast(msg: String, duration: Long = ToastHelper.DEFAULT_DURATION) {
    ToastHelper.instance.showDefaultToast(this, msg, duration)
}

fun Activity.showToast(view: View, duration: Long = ToastHelper.DEFAULT_DURATION) {
    ToastHelper.instance.showCustomToast(view, duration)
}

fun Fragment.blockToast(block: Boolean) {
    ToastHelper.instance.setBlockToast(block)
}