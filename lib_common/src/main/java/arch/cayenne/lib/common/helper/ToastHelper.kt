package arch.cayenne.lib.common.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import arch.cayenne.lib.common.databinding.ToastLayoutBinding

class ToastHelper private constructor() {
    companion object {
        private const val TAG = "ToastHelper"
        val instance: ToastHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ToastHelper() }
    }

    private var toast: Toast? = null

    fun showDefaultToast(context: Context, msg: String) {
        if (toast != null) {
            toast?.cancel()
        }
        val inflater = LayoutInflater.from(context)
        val layout = ToastLayoutBinding.inflate(inflater, null, false)

        layout.toastText.text = msg
        showToast(layout.root)
    }

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
            Handler(Looper.getMainLooper()).postDelayed({
                toast = null
            }, 2000L)
        }
    }

}
