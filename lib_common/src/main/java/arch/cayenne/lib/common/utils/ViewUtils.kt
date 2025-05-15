package arch.cayenne.lib.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.window.layout.WindowMetricsCalculator

object ViewUtils {

    @SuppressLint("ClickableViewAccessibility")
    fun hideKeyboard(context: Context, view: EditText, onClick:((v: View) -> Unit)? = null) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                onClick?.invoke(v)
            }
            true
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        val rect = Rect()
        val window = (context as? Activity)?.window
        window?.decorView?.getWindowVisibleDisplayFrame(rect)
        return rect.top
    }

    fun getNavigationBarHeight(context: Context): Int {
        val metrics = context.resources.displayMetrics
        val usableHeight = metrics.heightPixels

        val realMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context).bounds
        val realHeight = realMetrics.height()

        return if (realHeight > usableHeight) realHeight - usableHeight else 0
    }
}