package arch.cayenne.lib.common.helper

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import arch.cayenne.lib.common.databinding.ToastLayoutBinding

internal class ToastHelper private constructor() {
    companion object {
        private const val TAG = "ToastHelper"
        val instance: ToastHelper by lazy { ToastHelper() }
    }

    private var toast: Toast? = null

    /***
     * 預設toast
     * @param context
     * @param msg
     */
    fun showDefaultToast(context: Context, msg: String) {
        if (toast != null) {
            toast?.cancel()
        }
        val inflater = LayoutInflater.from(context)
        val layout = ToastLayoutBinding.inflate(inflater, null, false)

        layout.toastText.text = msg
        showToast(layout.root)
    }

    /***
     * 自定義toast
     * @param view 需先自行實作view
     */
    fun showCustomToast(view: View) {
        if (toast != null) {
            toast?.cancel()
        }
        showToast(view)
    }

    private fun showToast(view: View) {
        Toast(view.context).apply {
            duration = Toast.LENGTH_SHORT
            this.view = view
            setGravity(Gravity.CENTER, 0, 0)
            show()
            // 移除參考以允許下一次顯示（Toast.LENGTH_SHORT 約 2s）
            view.postDelayed({
                toast = null
            }, 2000L)
        }
    }

}

fun Fragment.showToast(msg: String) {
    ToastHelper.instance.showDefaultToast(requireContext(), msg)
}

fun Fragment.showToast(view: View) {
    ToastHelper.instance.showCustomToast(view)
}

fun Activity.showToast(msg: String) {
    ToastHelper.instance.showDefaultToast(this, msg)
}

fun Activity.showToast(view: View) {
    ToastHelper.instance.showCustomToast(view)
}