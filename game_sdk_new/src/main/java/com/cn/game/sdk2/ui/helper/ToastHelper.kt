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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ToastHelper {

    companion object {
        val instance: ToastHelper by lazy { ToastHelper() }
    }

    private var defaultY = 0
    private var canReplace = true
    private var job: Job? = null
    private var mToast: Toast? = null
    private var mHostToast: HostToastView? = null

    /**
     * @param offsetY 從上往下偏移，不設置則沿用前次顯示位置，橫向置中
     */
    fun showWindowToast(context: Context = ModuleInitializer.application, msg: String, offsetY: Int = defaultY, duration: Long = 2000, replace:Boolean = true) {
        if (!this.canReplace) return
        dismiss()
        this.canReplace = replace
        mToast = Toast.makeText(context, msg, duration.toInt()).apply {
            val view = HostToastView(context)
            this.view = view
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, offsetY)
            view.setMsg(msg)
            job = mainScope.launchWithCustomContext("showWindowToast") {
                delay(duration)
                this@apply.view = null
                dismiss()
            }
            show()
        }
    }

    fun showHostToast(attachView: View, msg: String, duration: Long = 2000, replace:Boolean = true) {
        if (!this.canReplace) return
        dismiss()
        this.canReplace = replace
        mHostToast = HostToastView(attachView.context).apply {
            show(attachView, msg, duration)
            post {
                val y = getToastY()
                val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                val statusBarHeight = if (resourceId > 0) {
                    context.resources.getDimensionPixelSize(resourceId)
                } else {
                    0
                }
                defaultY = y - statusBarHeight
            }
            job = mainScope.launchWithCustomContext("showHostToast") {
                delay(duration)
                dismiss()
            }
        }
    }

    private fun dismiss() {
        if (job?.isCompleted != true) {
            job?.cancel()
        }
        mToast?.cancel()
        mHostToast?.cancel()
        mToast = null
        mHostToast = null
        this.canReplace = true
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
        tag = hashCode()
    }

    fun show(host: View, msg: CharSequence, duration: Long) {
        this.scaleX = 0f
        this.scaleY = 0f
        val rootView = findParentView(host)
        binding.toastText.text = msg
        bindViewToParent(host, rootView)
        mainScope.launchWithCustomContext(TAG) {
            launch {
                playAnim(true)
                delay(duration)
                playAnim(false)
                dismissToast(rootView)
            }.join()
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

    private fun dismissToast(rootView: ViewGroup = findParentView(this)) {
        rootView.findViewWithTag<View>(hashCode())?.let {
            rootView.removeView(it)
        }
    }

    fun cancel() {
        visibility = View.GONE
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