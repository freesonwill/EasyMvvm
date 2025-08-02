package arch.cayenne.lib.common.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import arch.cayenne.lib.common.databinding.ToastLayoutBinding
import arch.cayenne.lib.common.utils.helper.toastAnim.ToastAnimation
import arch.cayenne.lib.common.utils.helper.toastAnim.ToastMessageAnimation
import arch.cayenne.lib.common.utils.helper.toastGesture.ToastGesture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.LinkedList
import java.util.Queue


class ToastHelper private constructor() {
    companion object {
        val instance: ToastHelper by lazy { ToastHelper() }
    }

    private val queueMap = mutableMapOf<String, Queue<ToastQueueItem>>()

    fun showToast(view: View, animInterface: ToastAnimation, toastGesture: ToastGesture?) {
        val tag = animInterface.getQueueTag() ?: animInterface.hashCode().toString()

        toastGesture?.let {
            initGesture(view, tag, it)
        }
        val item = createToastItem(view, animInterface, tag, toastGesture)
        showToastInternal(item, tag)
        checkToastInQueue(view, animInterface, tag)
        saveToastInQueue(item, tag)
    }

    private fun checkToastInQueue(view: View, animInterface: ToastAnimation, tag: String) {
        val currentQueue = queueMap[tag] ?: return
        if (!animInterface.isPlayQueueAnim()) {
            currentQueue.peek()?.let { item ->
                item.view?.let { v ->
                    val context = view.context
                    item.gesture?.clearGesture(v)
                    item.scope.launch {
                        item.animInterface.playDismissAnim(v)
                        removeToast(context, tag)
                    }
                }
            }
        } else {
            currentQueue.forEach { item ->
                item.view?.let { v ->
                    item.scope.launch {
                        item.animInterface.playQueueAnim(v)
                    }
                }
            }
        }
    }

    private fun saveToastInQueue(item: ToastQueueItem, tag: String) {
        if (!queueMap.containsKey(tag)) {
            queueMap[tag] = LinkedList()
        }
        val queue = queueMap[tag] ?: return
        queue.offer(item)
    }

    private fun createToastItem(view: View, animInterface: ToastAnimation, tag: String, toastGesture: ToastGesture?): ToastQueueItem {
        val context = view.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = animInterface.getLayoutParams(view)
        animInterface.onBeforeAddView(view)
        wm.addView(view, layoutParams)
        animInterface.onAfterAddView(view)

        val scope = CoroutineScope(Dispatchers.Main)
        return ToastQueueItem(WeakReference(view), scope, animInterface, toastGesture)
    }

    private fun showToastInternal(item: ToastQueueItem, tag: String) {
        val view = item.view ?: return
        val context = view.context
        val animInterface = item.animInterface
        val toastGesture = item.gesture
        item.scope.launch {
            animInterface.playShowAnim(view)
            delay(animInterface.showDuration)
            if (toastGesture == null || toastGesture.canAutoRemove()) {
                toastGesture?.clearGesture(view)
                animInterface.playDismissAnim(view)
                removeToast(context, tag)
            }
        }
    }

    private fun removeToast(context: Context, tag: String) {
        queueMap[tag]?.poll()?.let { item ->
            item.scope.cancel()
            item.view?.let {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                if (it.isAttachedToWindow) {
                    wm.removeView(it)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGesture(view: View, tag: String, gesture: ToastGesture) {
        gesture.setGesture(view)
        gesture.setForceRemoveListener {
            removeToast(view.context, tag)
        }
    }

    fun forceCancel() {
        queueMap.entries.forEach { queue ->
            queue.value.forEach { item ->
                item.scope.cancel()
                item.view?.let {
                    val wm = it.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    if (it.isAttachedToWindow) {
                        wm.removeView(it)
                    }
                }
            }
        }
        queueMap.clear()
    }

}

private data class ToastQueueItem(
    val viewHolder: WeakReference<View>,
    val scope: CoroutineScope,
    val animInterface: ToastAnimation,
    val gesture: ToastGesture?
) {
    val view: View? = viewHolder.get()
}

fun Fragment.showToast(msg: String?) {
    val inflater = LayoutInflater.from(requireActivity())
    val layout = ToastLayoutBinding.inflate(inflater, null, false)

    layout.toastText.text = msg
    showToast(layout.root, ToastMessageAnimation())
}

fun Fragment.showToast(view: View, toastAnimation: ToastAnimation, toastGesture: ToastGesture? = null) {
    ToastHelper.instance.showToast(view, toastAnimation, toastGesture)
}

fun Activity.showToast(msg: String) {
    val inflater = LayoutInflater.from(this)
    val layout = ToastLayoutBinding.inflate(inflater, null, false)

    layout.toastText.text = msg
    showToast(layout.root, ToastMessageAnimation())
}

fun Activity.showToast(view: View, toastAnimation: ToastAnimation, toastGesture: ToastGesture? = null) {
    ToastHelper.instance.showToast(view, toastAnimation, toastGesture)
}