package arch.cayenne.lib.common.utils.helper

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import arch.cayenne.lib.common.databinding.ToastLayoutBinding
import arch.cayenne.lib.common.utils.helper.toastAnim.ToastAnimation
import arch.cayenne.lib.common.utils.helper.toastAnim.ToastDefaultAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class ToastHelper private constructor() {
    companion object {
        val instance: ToastHelper by lazy { ToastHelper() }
    }

    private val queueMap = mutableMapOf<String, ToastQueueItem>()

    fun showToast(view: View, animInterface: ToastAnimation) {
        val tag = animInterface.getQueueTag() ?: animInterface.hashCode().toString()
        val context = view.context

        // 如果同 tag 已有 toast，先 dismiss
        queueMap[tag]?.let { item ->
            CoroutineScope(Dispatchers.Main).launch {
                item.view?.let {
                    item.animInterface.playDismissAnim(it)
                    cancelToast(context, tag)
                }
                // 等 dismiss 結束後再顯示新 toast
                showToastInternal(view, animInterface, tag)
            }
            return
        }
        showToastInternal(view, animInterface, tag)
    }

    private fun showToastInternal(view: View, animInterface: ToastAnimation, tag: String) {
        val context = view.context
        cancelToast(context, tag) // 保險起見
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = animInterface.getLayoutParams(view)
        animInterface.onBeforeAddView(view)
        wm.addView(view, layoutParams)
        animInterface.onAfterAddView(view)

        val job = CoroutineScope(Dispatchers.Main).launch {
            animInterface.playShowAnim(view)
            delay(animInterface.showDuration)
            animInterface.playDismissAnim(view)
            cancelToast(context, tag)
        }
        queueMap[tag] = ToastQueueItem(WeakReference(view), job, animInterface)
    }

    private fun cancelToast(context: Context, tag: String) {
        queueMap[tag]?.let { item ->
            item.job.cancel()
            item.view?.let {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                if (it.isAttachedToWindow) {
                    wm.removeView(it)
                }
            }
            queueMap.remove(tag)
        }
    }

    fun forceCancel() {
        queueMap.forEach { (_, item) ->
            item.job.cancel()
            item.view?.let {
                val wm = it.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                if (it.isAttachedToWindow) {
                    wm.removeView(it)
                }
            }
        }
        queueMap.clear()
    }

}

private data class ToastQueueItem(
    val viewHolder: WeakReference<View>,
    val job: Job,
    val animInterface: ToastAnimation
) {
    val view: View? = viewHolder.get()
}

fun Fragment.showToast(msg: String?) {
    val inflater = LayoutInflater.from(requireActivity())
    val layout = ToastLayoutBinding.inflate(inflater, null, false)

    layout.toastText.text = msg
    showToast(layout.root, ToastDefaultAnimation())
}

fun Fragment.showToast(view: View, toastAnimation: ToastAnimation) {
    ToastHelper.instance.showToast(view, toastAnimation)
}

fun Activity.showToast(msg: String) {
    val inflater = LayoutInflater.from(this)
    val layout = ToastLayoutBinding.inflate(inflater, null, false)

    layout.toastText.text = msg
    showToast(layout.root, ToastDefaultAnimation())
}

fun Activity.showToast(view: View, toastAnimation: ToastAnimation) {
    ToastHelper.instance.showToast(view, toastAnimation)
}