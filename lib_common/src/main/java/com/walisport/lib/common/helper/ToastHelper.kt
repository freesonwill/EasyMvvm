package com.walisport.lib.common.helper

import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import com.walisport.lib.common.databinding.ToastLayoutBinding
import com.walisport.lib.common.utils.ThreadUtils.launchWithCustomContext
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume

class ToastHelper {
    companion object {
        val instance: ToastHelper by lazy { ToastHelper() }
    }
    private val TAG = "ToastHelper"
    private var defaultY = 0
    private var canReplace = true
    private var mToastHolder:WeakReference<HostToastView>? = null
    private var mToast: HostToastView?
        set(value) { mToastHolder = if(value == null) null else WeakReference<HostToastView>(value) }
        get() = mToastHolder?.get()
    /**
     * @param offsetY 從上往下偏移，不設置則沿用前次顯示位置，橫向置中
     */
    private fun showWindowToast(context: Context, msg: String, offsetY: Int = defaultY, duration: Long = 2000, replace:Boolean = true) {
        if (!this.canReplace) return
        mToast?.dismissToast("cancel")
        this.canReplace = replace
        var windowManager:WindowManager? = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mToast = HostToastView(context, onDismiss = {
            windowManager?.removeView(mToast)
            mToast = null
            windowManager = null
            canReplace = true
        }).apply {
            showWindow(context, msg, offsetY, duration)
            defaultY = offsetY
        }
    }

    //Todo 此方法需要优化,不需要每次都传入attachView
    fun showHostToast(attachView: ViewGroup, msg: String, duration: Long = 2000, replace:Boolean = true) {
        if (!this.canReplace) {
            "ignore showHostToast ${mToast?.tag},${mToast?.msg}) is showing, ignore this $msg".loge(TAG)
            return
        }
        mToast?.dismissToast("cancel")
        this.canReplace = replace
        mToast = HostToastView(attachView.context, onDismiss =  {
            mToast = null
            canReplace = true
        }).apply {
            "show--->$msg,$duration,$tag,replace:$canReplace".logd(TAG)
            show(attachView, msg, duration)
        }
    }

}

private class HostToastView(context: Context,val onDismiss:(()->Unit)) : LinearLayout(context, null, 0) {
    private var mainScope: CoroutineScope? = CoroutineScope(Dispatchers.Main)
    private val TAG = "ToastHelper"
    private val binding: ToastLayoutBinding =
        ToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    val msg get() = binding.toastText.text

    init {
        gravity = Gravity.CENTER
        tag = hashCode()
        addOnAttachStateChangeListener(object :OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View) {
                "onViewAttachedToWindow:tag:$tag".logd(TAG)
            }
            override fun onViewDetachedFromWindow(p0: View) {
                removeOnAttachStateChangeListener(this)
                post{//这里需要延迟cancel,dismissToast在协程中,在协程中取消scope会引发CancellationException
                    "onViewDetachedFromWindow:tag:$tag, isActive:${mainScope?.isActive}".logd(TAG)
                    dismissToast()
                    mainScope?.cancel()
                    mainScope = null
                }
            }
        })
    }
    fun show(host: View, msg: CharSequence, duration: Long) {
        this.scaleX = 0f
        this.scaleY = 0f
        val rootView = findParentView(host)
        binding.toastText.text = msg
        bindViewToParent(host, rootView)
        mainScope!!.launchWithCustomContext(TAG) {
            playAnim(true)
            delay(duration)
            playAnim(false)
            dismissToast()
        }.invokeOnCompletion { cause->
            if (cause is CancellationException) {
                "showToast ${tag}:$msg has been canceled".loge(TAG)
            }
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
        mainScope!!.launchWithCustomContext(TAG) {
            playAnim(true)
            delay(duration)
            playAnim(false)
            dismissToast()
        }.invokeOnCompletion { cause->
            if (cause is CancellationException) {
                "showToast ${tag}:$msg has been canceled".loge(TAG)
            }
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
        return suspendCancellableCoroutine { continuation ->
            val start = if (show) 0f else 1f
            val end = if (!show) 0f else 1f
            val anim = ValueAnimator.ofFloat(start, end).apply {
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
                        //"${binding.toastText.text} anim was onCancel $tag".loge(TAG)
                        continuation.resume(-1)
                    },
                    onEnd = {
                        if (isCanceled) return@addListener
                        //"${binding.toastText.text} anim was onEnd $tag".loge(TAG)
                        continuation.resume(200)
                    }
                )
                start()
            }
            continuation.invokeOnCancellation {
                "${binding.toastText.text} was canceled $tag".loge(TAG)
                if(anim.isRunning) anim.cancel()
            }
        }
    }

    fun dismissToast(reason:String="normal") {
        if(parent == null) return
        "dismissToast-->$msg,${tag} reason:$reason".logd(TAG)
        (parent as ViewGroup).removeView(this)
        onDismiss.invoke()
    }

    private fun findParentView(view: View): ViewGroup {
        if (view is ConstraintLayout || view is RelativeLayout || view is FrameLayout) return view as ViewGroup
        return findParentView(view.parent as View)
    }

    private fun bindViewToParent(view: View, parent: ViewGroup) {
        when (parent) {
            is ConstraintLayout -> {
                val lp = ConstraintLayout.LayoutParams(0,0)
                lp.startToStart = view.id
                lp.endToEnd = view.id
                lp.topToTop = view.id
                lp.bottomToBottom = view.id
                parent.addView(this, lp)
            }

            is LinearLayout -> {
                //移除LinearLayout，因為LinearLayout無法實現疊加畫面
                //val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                //lp.gravity = Gravity.CENTER
                //parent.addView(view, lp)
                throw IllegalStateException("not supported parent:$parent")
            }

            is RelativeLayout -> {
                val lp = RelativeLayout.LayoutParams(0,0)
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
                val layoutParams = FrameLayout.LayoutParams(0,0)

                // 設置 newView 的 margin 使其中心點對齊 targetView 的中心點
                layoutParams.leftMargin = relativeCenterX - this.width / 2
                layoutParams.topMargin = relativeCenterY - this.height / 2

                val lp = FrameLayout.LayoutParams(0, 0)
                lp.gravity = Gravity.CENTER
                parent.addView(this, lp)
            }

            else -> throw IllegalStateException("not supported parent:$parent")
        }
    }
}
