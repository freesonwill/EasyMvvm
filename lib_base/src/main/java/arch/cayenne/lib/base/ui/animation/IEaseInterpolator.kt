package arch.cayenne.lib.base.ui.animation

import android.view.animation.Interpolator

/**
 * @date: 2025/8/12 16:12
 * @description: EaseInterpolator接口
 */
interface IEaseInterpolator {
    fun easeIn(): Interpolator
    fun easeOut(): Interpolator
    fun easeInOut(): Interpolator
}