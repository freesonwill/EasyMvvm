package arch.cayenne.lib.common.utils

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

object FadeAnimation {

    private const val DEFAULT_DURATION = 300L

    // 渐变显示
    fun fadeIn(
        view: View
    ){
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(DEFAULT_DURATION)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    // 渐变隐藏
    fun fadeOut(
        view: View?,
        onEnd: (() -> Unit)? = null
    ) {
        // 淡出动画
        view?.animate()
            ?.alpha(0f)
            ?.setDuration(DEFAULT_DURATION)
            ?.setInterpolator(AccelerateInterpolator())
            ?.withEndAction {
                onEnd?.invoke()
            }
            ?.start()
    }


}