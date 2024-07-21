package com.cn.game.sdk2.data

import android.animation.Animator
import android.view.ViewPropertyAnimator

data class BetteFlyData(
    val animator: ViewPropertyAnimator,
    var isRunning: Boolean = false
) {
}