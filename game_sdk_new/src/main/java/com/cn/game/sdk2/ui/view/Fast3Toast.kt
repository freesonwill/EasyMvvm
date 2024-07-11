package com.cn.game.sdk2.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.ToastLayoutBinding
import com.xcjh.base_lib2.utils.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class Fast3Toast @JvmOverloads constructor(
    context: Context,
    private val anchorView:ConstraintLayout,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    companion object {
        const val TAG = "Fast3Toast"
    }
    init {
        gravity = Gravity.CENTER
    }
    private var binding: ToastLayoutBinding = ToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private var dismissRunnable:Runnable? = null
    private var animator:Animator? = null
    //前一个toast能否被替换
    private var canReplace = true
    private val scope = CoroutineScope(Dispatchers.Main)
    private val H:Handler = Handler(Looper.getMainLooper())
    /**
     *
     */
    @JvmOverloads
    fun showToastNormal(msg: CharSequence, duration: Long, replace:Boolean = true) {
        if(!this.canReplace) return
        dismissToast()
        this.canReplace = replace
        val lp = ConstraintLayout.LayoutParams(0, 0)
        binding.toastText.text = msg
        lp.startToStart = R.id.viewPagerNew
        lp.endToEnd = R.id.viewPagerNew
        lp.topToTop = R.id.viewPagerNew
        lp.bottomToBottom = R.id.viewPagerNew
        anchorView.addView(this, lp)
        playAnim(true,msg)
        dismissRunnable = Runnable {
            playAnim(false,msg){
                dismissToast()
            }
        }.also {
            H.postDelayed(it, duration)
        }
    }

    //Todo 改造协程
    private fun playAnim(show:Boolean,msg:CharSequence,onEnd:(()->Unit)? = null){
        animator.let {
            animator?.cancel()
            val start = if(show) 0f else 1f;
            val end = if(!show) 0f else 1f;
            ValueAnimator.ofFloat(start,end).apply {
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
                        animator = it
                        //LogUtils.dTag(TAG,"animator onStart${msg}:${animator.hashCode()},canReplace:${canReplace}")
                    },
                    onCancel = {
                        //LogUtils.dTag(TAG,"animator onCancel${msg}:${animator.hashCode()},canReplace:${canReplace}")
                        animator = null
                        isCanceled = true
                    },
                    onEnd = {
                        //LogUtils.dTag(TAG,"animator onEnd${msg}:${animator.hashCode()},isCanceled:${isCanceled},canReplace:${canReplace}")
                        if(isCanceled) return@addListener
                        animator = null
                        onEnd?.invoke()
                    }
                )
                start()
            }
        }
    }

    fun dismissToast() {
        //LogUtils.dTag(TAG,"dismissToast:${animator.hashCode()}")
        animator?.cancel()
        anchorView.removeView(this)
        if(dismissRunnable != null) H.removeCallbacks(dismissRunnable!!).let {  dismissRunnable = null }
        this.canReplace = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}
