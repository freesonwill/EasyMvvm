package com.cn.game.sdk2.ui.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.lxj.xpopup.animator.PopupAnimator

class AlphaPopupAnimator(target: View, animationDuration: Int, private val alpha: FloatArray) : PopupAnimator(target, animationDuration) {
    override fun initAnimator() {
        targetView.alpha = alpha[0]
    }

    override fun animateShow() {
        val animator: ViewPropertyAnimator? = targetView.animate().alpha(alpha[alpha.size - 1])
        animator?.setInterpolator(FastOutSlowInInterpolator())?.setDuration(
            animationDuration.toLong()
        )?.withLayer()?.start()
    }

    override fun animateDismiss() {
        if (!this.animating) {
            val animator: ViewPropertyAnimator? = targetView.animate().alpha(this.alpha[0])
            if (animator != null) {
                this.observerAnimator(
                    animator.setInterpolator(FastOutSlowInInterpolator())
                        .setDuration((animationDuration.toDouble() * 0.8).toLong())
                        .withLayer()
                ).start()
            }
        }
    }
}