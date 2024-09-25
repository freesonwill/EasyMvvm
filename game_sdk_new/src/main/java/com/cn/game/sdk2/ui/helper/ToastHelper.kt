package com.cn.game.sdk2.ui.helper

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import com.cn.game.sdk2.databinding.ToastLayoutBinding
import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.utils.ThreadUtils.mainScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ToastHelper {

    companion object {
        val instance: ToastHelper by lazy { ToastHelper() }
    }

    private var defaultY = 0
    private var canReplace = true
    private var job: Job? = null
    private var windowManager: WindowManager? = null
    private var mToast: HostToastView? = null

    /**
     * @param offsetY 從上往下偏移，不設置則沿用前次顯示位置，橫向置中
     */
    fun showWindowToast(context: Context, msg: String, offsetY: Int = defaultY, duration: Long = 3000, replace:Boolean = true) {
        if (!this.canReplace) return
        dismiss()
        this.canReplace = replace
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mToast = HostToastView(context).apply {
            showWindow(context, msg, offsetY, duration)
            defaultY = offsetY
            job = mainScope.launchWithCustomContext("showWindowToast") {
                delay(duration)
                dismiss()
            }
        }
    }

    fun showHostToast(attachView: View, msg: String, duration: Long = 3000, replace:Boolean = true) {
        if (!this.canReplace) return
        dismiss()
        this.canReplace = replace
        mToast = HostToastView(attachView.context).apply {
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

        windowManager?.removeView(mToast)
        windowManager = null
        mToast = null
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
            playAnim(true)
            delay(duration)
            playAnim(false)
            dismissToast(rootView)
        }
    }

    fun showWindow(context: Context, msg: CharSequence, offsetY: Int = 0, duration: Long) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()

        // 设置参数
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        setMsg(msg)
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = offsetY
        wm.addView(this, layoutParams)
        mainScope.launchWithCustomContext(TAG) {
            playAnim(true)
            delay(duration)
            playAnim(false)
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
                    alpha = p
                    scaleY = p
                }
                var isCanceled = false
                addListener(
                    onStart = {
                        scaleX = start
                        alpha = start
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
        if (view is ConstraintLayout || view is RelativeLayout || view is FrameLayout) return view as ViewGroup
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
                // 移除LinearLayout，因為LinearLayout無法實現疊加畫面
//                val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//                lp.gravity = Gravity.CENTER
//                parent.addView(view, lp)
            }

            is RelativeLayout -> {
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.addRule(RelativeLayout.ALIGN_TOP, view.id)
                lp.addRule(RelativeLayout.ALIGN_BOTTOM, view.id)
                lp.addRule(RelativeLayout.ALIGN_START, view.id)
                lp.addRule(RelativeLayout.ALIGN_END, view.id)
                parent.addView(this, lp)
            }

            is FrameLayout -> {
                val targetLocation = IntArray(2)
                view.getLocationOnScreen(targetLocation) // 或 getLocationInWindow()

                // 計算 targetView 的中心點
                val targetCenterX = targetLocation[0] + view.width / 2
                val targetCenterY = targetLocation[1] + view.height / 2

                // 獲取 parent 的位置，因為 getLocationOnScreen 返回的是相對螢幕的座標
                val parentLocation = IntArray(2)
                parent.getLocationOnScreen(parentLocation)

                // 計算相對於 parent 的中心點位置
                val relativeCenterX = targetCenterX - parentLocation[0]
                val relativeCenterY = targetCenterY - parentLocation[1]

                // 設置 newView 的 LayoutParams，讓其中心點對齊到 targetView 的中心點
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )

                // 設置 newView 的 margin 使其中心點對齊 targetView 的中心點
                layoutParams.leftMargin = relativeCenterX - this.width / 2
                layoutParams.topMargin = relativeCenterY - this.height / 2

                val lp = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                lp.gravity = Gravity.CENTER
                parent.addView(this, lp)
            }

            else -> {

            }
        }
    }
}