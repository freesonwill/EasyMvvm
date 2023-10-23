package com.xcjh.app.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView

fun startImageRotate(imageView: ImageView, toggle: Boolean) {
    val tarRotate: Float = if (toggle) {
        180f
    } else {
        0f
    }

    imageView.apply {
        ObjectAnimator.ofFloat(this, "rotation", rotation, tarRotate).let {
            it.duration = 300
            it.start()
        }
    }
}

/**
 * 时间跳动动画
 */
fun startTimeAnimator(view: View) {

    val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
    fadeIn.duration = 500
    fadeIn.startDelay = 200 // 延迟200毫秒开始动画

    val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
    fadeOut.duration = 500
    fadeOut.startDelay = 200 // 延迟200毫秒开始动画

    val animatorSet = AnimatorSet()
    animatorSet.playSequentially(fadeIn, fadeOut) // 顺序播放渐显和渐隐动画
    animatorSet.startDelay = 200 // 延迟200毫秒开始第一次播放动画
    animatorSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            // 动画结束时重新播放
            super.onAnimationEnd(animation)
            animatorSet.start()
        }
    })
    animatorSet.start()

}