package arch.cayenne.lib.base.ui.animation

import android.view.animation.Interpolator

/**
 * @date: 2025/8/11
 * @description: 📦 三次缓动插值器集合
 *
 * - EaseInCubic:   慢进快出
 * - EaseOutCubic:  快进慢出
 * - EaseInOutCubic: 慢进快中快出慢
 */
object EaseCubicInterpolator {

    /**
     * 🚀 缓入（三次缓入）
     * 公式: f(x) = x³
     */
    class EaseIn : Interpolator {
        override fun getInterpolation(x: Float): Float {
            return x * x * x
        }
    }

    /**
     * 🚀 缓出（三次缓出）
     * 公式: f(x) = 1 - (1 - x)³
     */
    class EaseOut : Interpolator {
        override fun getInterpolation(x: Float): Float {
            val t = 1f - x
            return 1f - t * t * t
        }
    }

    /**
     * 🚀 缓入缓出（三次缓入缓出）
     * 公式:
     *   if x < 0.5: 4x³
     *   else: 1 - ((-2x + 2)³ / 2)
     */
    class EaseInOut : Interpolator {
        override fun getInterpolation(x: Float): Float {
            return if (x < 0.5f) {
                4f * x * x * x
            } else {
                val t = -2f * x + 2f
                1f - (t * t * t) / 2f
            }
        }
    }
}
