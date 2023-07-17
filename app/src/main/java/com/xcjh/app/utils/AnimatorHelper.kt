package com.xcjh.app.utils

import android.animation.ObjectAnimator
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