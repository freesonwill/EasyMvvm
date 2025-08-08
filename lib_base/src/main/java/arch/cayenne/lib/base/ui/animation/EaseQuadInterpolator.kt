package arch.cayenne.lib.base.ui.animation

import android.view.animation.Interpolator

/**
 * @date: 2025/8/8 11:20
 * @description: 📦 二次缓动插值器集合
 *
 * - EaseInQuad:   慢进快出
 * - EaseOutQuad:  快进慢出
 * - EaseInOutQuad: 慢进快中快出慢
 */
object EaseQuadInterpolator {

    /**
     * 🚀 缓入（二次缓入）
     * 公式: f(x) = x²
     */
    class EaseIn : Interpolator {
        override fun getInterpolation(x: Float): Float {
            return x * x
        }
    }

    /**
     * 🚀 缓出（二次缓出）
     * 公式: f(x) = 1 - (1 - x)²
     */
    class EaseOut : Interpolator {
        override fun getInterpolation(x: Float): Float {
            return 1f - (1f - x) * (1f - x)
        }
    }

    /**
     * 🚀 缓入缓出（二次缓入缓出）
     * 公式:
     *   if x < 0.5: 2x²
     *   else: -1 + (4 - 2x) * x
     */
    class EaseInOut : Interpolator {
        override fun getInterpolation(x: Float): Float {
            return if (x < 0.5f) {
                2f * x * x
            } else {
                -1f + (4f - 2f * x) * x
            }
        }
    }
}
