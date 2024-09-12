package com.cn.game.sdk2.ui.helper

import android.animation.ValueAnimator
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import com.cn.game.sdk2.databinding.ToastLayoutBinding
import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.utils.ThreadUtils.mainScope
import com.xcjh.base_lib2.ModuleInitializer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ToastHelper {

    companion object {
        private const val TAG = "ToastHelper"
        val instance: ToastHelper by lazy { ToastHelper() }
    }

    private var defaultY = 0

    /**
     * @param offsetY 從上往下偏移，不設置則沿用前次顯示位置，橫向置中
     */
    fun showWindowToast(context: Context = ModuleInitializer.application, msg: String, offsetY: Int = defaultY, duration: Long = 2000, canReplace:Boolean = true) {
        val toast = Toast.makeText(context, msg, duration.toInt())
        val view = HostToastView(context)
        toast.view = view
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, offsetY)
        view.setMsg(msg)
        mainScope.launchWithCustomContext("showWindowToast") {
            delay(duration)
            toast.view = null
        }
        toast.show()
    }

    fun showHostToast(attachView: View, msg: String, duration: Long = 2000, canReplace:Boolean = true) {
        val view = HostToastView(attachView.context)
        view.show(attachView, msg, duration)
        view.post {
            val context = view.context
            val y = view.getToastY()
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) {
                context.resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
            defaultY = y - statusBarHeight
        }
    }
}

private class HostToastView(context: Context) : LinearLayout(context, null, 0) {
    companion object {
        private const val TAG = "HostToastView"
    }

    private val binding: ToastLayoutBinding =
        ToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        gravity = Gravity.CENTER
        tag = TAG
    }

    fun show(host: View, msg: CharSequence, duration: Long) {
        this.scaleX = 0f
        this.scaleY = 0f
        val rootView = findParentView(host)
        binding.toastText.text = msg
        bindViewToParent(host, rootView)
        mainScope.launchWithCustomContext(TAG) {
            val d1 = launch { playAnim(true) }
            val d2 = launch {
                delay(duration)
                playAnim(false)
                dismissToast(rootView)
            }
            d1.join()
            d2.join()
        }
    }

    fun setMsg(msg: CharSequence) {
        binding.toastText.text = msg
    }

    fun getToastY(): Int {
        val location = IntArray(2)
        binding.root.getLocationInWindow(location)
        return location[1] - binding.root.height / 2
    }

    private suspend fun playAnim(show: Boolean): Int {
        return suspendCoroutine { continuation ->
            val start = if (show) 0f else 1f
            val end = if (!show) 0f else 1f
            ValueAnimator.ofFloat(start, end).apply {
                duration = 150
                addUpdateListener { animation ->
                    val p = animation.animatedValue as Float
                    scaleX = p
                    scaleY = p
                }
                var isCanceled = false
                addListener(
                    onStart = {
                        scaleX = start
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
        }
    }

    fun dismissToast(rootView: ViewGroup = findParentView(this)) {
        rootView.findViewWithTag<View>(TAG)?.let {
            rootView.removeView(it)
        }
    }

    private fun findParentView(view: View): ViewGroup {
        if (view is ConstraintLayout || view is LinearLayout || view is RelativeLayout) return view as ViewGroup
        return findParentView(view.parent as View)
    }

    private fun bindViewToParent(view: View, parent: ViewGroup) {
        when (parent) {
            is ConstraintLayout -> {
                val lp = ConstraintLayout.LayoutParams(0, 0)
                lp.startToStart = view.id
                lp.endToEnd = view.id
                lp.topToTop = view.id
                lp.bottomToBottom = view.id
                parent.addView(this, lp)
            }

            is LinearLayout -> {
                //TODO
            }

            is RelativeLayout -> {
                //TODO
            }

            else -> {

            }
        }
    }
}