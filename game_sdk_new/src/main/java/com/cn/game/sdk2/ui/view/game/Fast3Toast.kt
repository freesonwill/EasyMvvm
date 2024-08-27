package com.cn.game.sdk2.ui.view.game

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.ToastLayoutBinding
import com.cn.game.sdk2.utils.ThreadUtils
import com.xcjh.base_lib2.utils.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Fast3Toast @JvmOverloads constructor(
    context: Context,
    val anchorView: ConstraintLayout,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "Fast3Toast"
    }

    init {
        gravity = Gravity.CENTER
    }

    private var binding: ToastLayoutBinding =
        ToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    //前一个toast能否被替换
    private var canReplace = true

    private var job: Job? = null
    private val exceptionHandler:CoroutineExceptionHandler = CoroutineExceptionHandler {coroutineContext,throwable->
        LogUtils.dTag(TAG,"Caught exception in CoroutineExceptionHandler: $throwable")
    }
    private var msg:CharSequence = ""

    /**
     * 显示Toast
     */
    @JvmOverloads
    fun showToastNormal(msg: CharSequence, duration: Long, replace: Boolean = true) {
        if (!this.canReplace) return
        dismissToast()
        this.canReplace = replace
        val lp = ConstraintLayout.LayoutParams(0, 0)
        binding.toastText.text = msg
        this.msg = msg
        lp.startToStart = R.id.viewPagerNew
        lp.endToEnd = R.id.viewPagerNew
        lp.topToTop = R.id.viewPagerNew
        lp.bottomToBottom = R.id.viewPagerNew
        anchorView.addView(this, lp)
        job = ThreadUtils.mainScope.launch(exceptionHandler) {
            val d1 = async { playAnim(true) }
            val d2 = async {
                d1.await()
                delay(duration)
                playAnim(false)
                dismissToast()
            }
            d1.await()
            d2.await()
        }
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
                //cancel会触发onCancel和onEnd,需要标准位判断
                var isCanceled = false
                addListener(
                    onStart = {
                        scaleX = start
                        scaleY = start
                        //animator = it
                        //LogUtils.dTag(TAG,"animator onStart${msg}:${job.hashCode()},canReplace:${canReplace}")
                    },
                    onCancel = {
                        //LogUtils.dTag(TAG,"animator onCancel${msg}:${job.hashCode()},canReplace:${canReplace}")
                        isCanceled = true
                        continuation.resume(-1)
                    },
                    onEnd = {
                        //LogUtils.dTag(TAG,"animator onEnd${msg}:${job.hashCode()},isCanceled:${isCanceled},canReplace:${canReplace}")
                        if (isCanceled) return@addListener
                        continuation.resume(200)
                    }
                )
                start()
            }
        }
    }

    fun dismissToast() {
        //LogUtils.dTag(TAG,"dismissToast:${job.hashCode()}")
        anchorView.removeView(this)
        this.canReplace = true
        if (job?.isCompleted != true) job?.cancel()
        job = null
    }
}
