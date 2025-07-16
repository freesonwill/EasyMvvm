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

    private var toastJob: Job? = null
    //方便cancelToast
    private var viewHolder:WeakReference<View>? = null
    private var view:View?
        get() = viewHolder?.get()
        set(value) { viewHolder = if(value == null) null else WeakReference(value) }

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

    fun showToast(view: View, animInterface: ToastAnimation) {
        cancelToast(view.context)
        if (toastJob != null) {
            return
        }

        this.view = view
        val wm = view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = animInterface.getLayoutParams()
        animInterface.onBeforeAddView(view)
        wm.addView(view, layoutParams)
        animInterface.onAfterAddView(view)

        toastJob = CoroutineScope(Dispatchers.Main).launch {
            animInterface.playShowAnim(view)
            delay(animInterface.showDuration)
            animInterface.playDismissAnim(view)
            cancelToast(view.context)
        }
    }

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