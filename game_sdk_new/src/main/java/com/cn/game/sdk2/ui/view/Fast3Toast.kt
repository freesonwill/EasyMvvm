package com.cn.game.sdk2.ui.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Fast3Toast @JvmOverloads constructor(
    context: Context,
    private val anchorView:ConstraintLayout,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
        gravity = Gravity.CENTER
    }
    private var binding: ToastLayoutBinding = ToastLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private var dismissRunnable:Runnable? = null
    private var animator:Animator? = null

    fun showToastNormal(msg: CharSequence, duration: Long) {
        dismissToast()
        val lp = ConstraintLayout.LayoutParams(0, 0)
        binding.toastText.text = msg
        lp.startToStart = R.id.viewPagerNew
        lp.endToEnd = R.id.viewPagerNew
        lp.topToTop = R.id.viewPagerNew
        lp.bottomToBottom = R.id.viewPagerNew
        anchorView.addView(this, lp)
        playAnim(true)
        dismissRunnable = kotlinx.coroutines.Runnable {
            playAnim(false){
                dismissToast()
                dismissRunnable = null
            }
        }
        postDelayed(dismissRunnable, duration)
    }

    //Todo 改造协程
    private fun playAnim(show:Boolean,onEnd:(()->Unit)? = null){
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
                addListener(
                    onStart = {
                        scaleX = start
                        scaleY = start
                        animator = it
                    },
                    onCancel = { animator = null},
                    onEnd = {
                        animator = null
                        onEnd?.invoke()
                    }
                )
                start()
            }
        }
    }

    fun dismissToast() {
        anchorView.removeView(this)
        if(dismissRunnable != null) removeCallbacks(dismissRunnable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}
