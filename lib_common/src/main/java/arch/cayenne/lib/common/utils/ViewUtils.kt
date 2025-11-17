package arch.cayenne.lib.common.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.drawToBitmap

object ViewUtils {
    private const val TAG = "ViewUtils"

    fun hideKeyboard(context: Context, view: EditText, onClick: ((v: View) -> Unit)? = null) {
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
        val key =
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
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
    fun collapseView(view: View, fakerView: ImageView,onEnd: (() -> Unit)? = null) {
        val snapshot = view.drawToBitmap()
        fakerView.setImageBitmap(snapshot)

        val viewHeight = view.height

        val animator = ValueAnimator.ofInt(viewHeight, 1)
        animator.duration = 2000L
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            fakerView.layoutParams = fakerView.layoutParams.apply { height = animatedValue }
        }
        animator.doOnStart {
            fakerView.layoutParams = fakerView.layoutParams.apply { height = viewHeight }
            fakerView.visibility = View.VISIBLE
            view.visibility = View.GONE
        }

        animator.doOnEnd {
            fakerView.visibility = View.GONE
            onEnd?.invoke()
        }

        animator.start()
    }

    /**
     * 展开View
     * bug: 对于一开始Gone的View，drawToBitmap()会崩溃；即使使用measure来获取，获取的图片也偏大
     * @param view
     * @param fakerView
     * @param onEnd
     */
    fun expandView(view: View, fakerView: ImageView,onEnd:(()->Unit)? = null) {
        // 把實際要展出的畫面先截圖給 fakerView
        view.visibility = View.GONE

        // 把實際要展出的畫面先截圖給 fakerView
        val snapshot = view.drawToBitmap()
        fakerView.setImageBitmap(snapshot)

        // 先設為 0 高度，逐步展開
        val viewHeight = snapshot.height
        val animator = ValueAnimator.ofInt(0, viewHeight)
        animator.duration = 200L
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            fakerView.layoutParams = fakerView.layoutParams.apply { height = animatedValue }
        }

        animator.doOnStart {
            fakerView.layoutParams = fakerView.layoutParams.apply { height = 0 }
            fakerView.visibility = View.VISIBLE
            view.visibility = View.GONE
        }

        animator.doOnEnd  {
            // 展開完成後切回原始 view，隱藏 fakerView
            fakerView.layoutParams = fakerView.layoutParams.apply { height = viewHeight }
            view.visibility = View.VISIBLE
            fakerView.visibility = View.GONE
            onEnd?.invoke()
        }

        animator.start()
    }

    fun expandView(view: ImageView, expand: Boolean, onEnd: (() -> Unit)? = null) {
        val start = if (expand) 0f else 180f
        val end = if (expand) 180f else 0f
        ObjectAnimator.ofFloat(view, "rotation", start, end)
            .also {
                it.interpolator = LinearInterpolator()
                it.duration = 150
                it.addListener(onEnd = {
                    onEnd?.invoke()
                })
                it.start()
            }
    }

    /**
     * 将 [childView] 从当前父视图中移除，并提到与 [parentView] 同级，
     * 并在父布局中尽量与 [parentView] 对齐。
     */
    fun moveViewToSameLevel(childView: View, parentView: View) {
        // 确保 childView 当前是 targetView 的子 View
        if (childView.parent == parentView && parentView is ViewGroup) {
            parentView.removeView(childView)

            val parent = parentView.parent as? ViewGroup ?: return

            val lp: ViewGroup.LayoutParams = when (parent) {
                is ConstraintLayout -> ConstraintLayout.LayoutParams(0, 0).apply {
                    startToStart = parentView.id
                    endToEnd = parentView.id
                    topToTop = parentView.id
                    bottomToBottom = parentView.id
                }

                is FrameLayout -> FrameLayout.LayoutParams(
                    parentView.width,
                    parentView.height
                ).apply {
                    leftMargin = parentView.left
                    topMargin = parentView.top
                }

                is RelativeLayout -> RelativeLayout.LayoutParams(
                    parentView.width,
                    parentView.height
                ).apply {
                    addRule(RelativeLayout.ALIGN_TOP, parentView.id)
                    addRule(RelativeLayout.ALIGN_BOTTOM, parentView.id)
                    addRule(RelativeLayout.ALIGN_START, parentView.id)
                    addRule(RelativeLayout.ALIGN_END, parentView.id)
                }

                else -> ViewGroup.LayoutParams(
                    parentView.width,
                    parentView.height
                ).also  {
                    childView.x = parentView.x
                    childView.y = parentView.y
                }
            }

            parent.addView(childView, lp)
        }
    }
}