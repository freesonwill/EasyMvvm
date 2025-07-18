package com.walisport.module.search.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View

object IconScaleAnimUtil {
    private const val IMAGE_SCALE_RATIO = 1.2f
    private const val IMAGE_SCALE_DURATION = 100L
    private var scaleXAnimation: ObjectAnimator? = null
    private var scaleYAnimation: ObjectAnimator? = null

    @SuppressLint("ClickableViewAccessibility")
    fun View.enableScaleIcon() {
        setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                ACTION_DOWN -> {
                    if(isEnabled) scaleIcon(view, false)
                }
                ACTION_UP, ACTION_CANCEL -> {
                    if(isEnabled) scaleIcon(view, true)
                }
                else -> Unit
            }
            return@setOnTouchListener false
        }
    }

    private fun scaleIcon(view: View, isReverse: Boolean) {
        val (start, end) = if (isReverse) {
            Pair(IMAGE_SCALE_RATIO, 1.0f)
        } else {
            Pair(1.0f, IMAGE_SCALE_RATIO)
        }
        scaleXAnimation?.cancel()
        scaleYAnimation?.cancel()
        scaleXAnimation = ObjectAnimator.ofFloat(view, "scaleX", start, end).apply {
            this.duration = IMAGE_SCALE_DURATION
            start()
        }
        scaleYAnimation = ObjectAnimator.ofFloat(view, "scaleY", start, end).apply {
            this.duration = IMAGE_SCALE_DURATION
            start()
        }
    }
}