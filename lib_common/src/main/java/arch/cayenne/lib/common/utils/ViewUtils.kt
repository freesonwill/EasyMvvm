package arch.cayenne.lib.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

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
        val key = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                "navigation_bar_height"
            } else {
                "navigation_bar_height_landscape"
            }
        return getInternalDimensionSize(context, key)
    }

    private fun getInternalDimensionSize(context: Context, key: String): Int {
        val result = 0
        try {
            val resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android")
            if (resourceId > 0) {
                val sizeOne = context.resources.getDimensionPixelSize(resourceId)
                val sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId)

                if (sizeTwo >= sizeOne && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                            key != "status_bar_height")
                ) {
                    return sizeTwo
                } else {
                    val densityOne = context.resources.displayMetrics.density
                    val densityTwo = Resources.getSystem().displayMetrics.density
                    val f = sizeOne * densityTwo / densityOne
                    return (if ((f >= 0)) (f + 0.5f) else (f - 0.5f)).toInt()
                }
            }
        } catch (ignored: Resources.NotFoundException) {
            return 0
        }
        return result
    }
}