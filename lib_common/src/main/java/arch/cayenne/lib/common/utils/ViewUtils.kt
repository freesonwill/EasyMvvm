package arch.cayenne.lib.common.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.drawToBitmap

object ViewUtils {

    @SuppressLint("ClickableViewAccessibility")
    fun hideKeyboard(context: Context, view: EditText, onClick:((v: View) -> Unit)? = null) {
        view.showSoftInputOnFocus = false
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.setOnClickListener { v ->
            onClick?.invoke(v)
        }
        view.post {
            view.isCursorVisible = true
            view.requestFocus()
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

    // 必須使用faker view才能解決子view設置0dp會顯示錯誤問題
    fun collapseView(view: View, fakerView: ImageView) {
        val snapshot = view.drawToBitmap()
        fakerView.setImageBitmap(snapshot)

        val initialHeight = view.height

        val animator = ValueAnimator.ofInt(initialHeight, 1)
        animator.duration = 200L
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = fakerView.layoutParams
            layoutParams.height = animatedValue
            fakerView.layoutParams = layoutParams
        }
        animator.doOnStart {
            val layoutParams = fakerView.layoutParams
            layoutParams.height = initialHeight
            fakerView.layoutParams = layoutParams
            fakerView.visibility = View.VISIBLE
            view.visibility = View.GONE
        }

        animator.doOnEnd {
            fakerView.visibility = View.GONE
        }

        animator.start()
    }

    fun expandView(view: View, fakerView: ImageView) {

        // 先確保原始 view 是隱藏狀態
        view.visibility = View.GONE

        // 把實際要展出的畫面先截圖給 fakerView
        val snapshot = view.drawToBitmap()
        fakerView.setImageBitmap(snapshot)

        // 先設為 0 高度，逐步展開
        val targetHeight = snapshot.height
        val animator = ValueAnimator.ofInt(1, targetHeight)
        animator.duration = 200L
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = fakerView.layoutParams
            layoutParams.height = animatedValue
            fakerView.layoutParams = layoutParams
        }

        animator.doOnStart {
            val layoutParams = fakerView.layoutParams
            layoutParams.height = 0
            fakerView.layoutParams = layoutParams
            fakerView.visibility = View.VISIBLE
        }

        animator.doOnEnd {
            // 展開完成後切回原始 view，隱藏 fakerView
            fakerView.visibility = View.GONE
            view.visibility = View.VISIBLE
        }

        animator.start()
    }
}