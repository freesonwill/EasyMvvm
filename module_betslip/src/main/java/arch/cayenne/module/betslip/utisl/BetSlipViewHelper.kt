package arch.cayenne.module.betslip.utisl

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener

internal object BetSlipViewHelper {

    fun collapseView(view: View, onEnd: (() -> Unit)? = null) {
        val height = view.height
        ObjectAnimator.ofFloat(view, "translationY", 0f, height.toFloat())
            .also {
                it.interpolator = LinearInterpolator()
                it.duration = 200
                it.addListener(onEnd = {
                    onEnd?.invoke()
                })
                it.start()
            }
    }

    fun expandView(view: View, height: Float, onEnd: (() -> Unit)? = null) {
        ObjectAnimator.ofFloat(view, "translationY", height, 0f)
            .also {
                it.interpolator = LinearInterpolator()
                it.duration = 200
                it.addListener(onEnd = {
                    onEnd?.invoke()
                })
                it.start()
            }
    }
}