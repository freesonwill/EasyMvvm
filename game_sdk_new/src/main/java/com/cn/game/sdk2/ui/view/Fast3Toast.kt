package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.ToastLayoutBinding

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


    fun showToastNormal(msg: CharSequence, duration: Long) {
        dismissToast()
        val lp = ConstraintLayout.LayoutParams(0, 0)
        binding.toastText.text = msg
        lp.startToStart = R.id.viewPagerNew
        lp.endToEnd = R.id.viewPagerNew
        lp.topToTop = R.id.viewPagerNew
        lp.bottomToBottom = R.id.viewPagerNew
        anchorView.addView(this, lp)
        dismissRunnable = kotlinx.coroutines.Runnable {
            dismissToast()
            dismissRunnable = null
        }
        postDelayed(dismissRunnable, duration)
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
