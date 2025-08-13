package arch.cayenne.lib.base.ui.animation

import android.view.animation.Interpolator

/**
 * @date: 2025/8/11
 * @description: 📦 三次缓动插值器
 *
 * - easeIn:   慢进快出
 * - easeOut:  快进慢出
 * - easeOut: 慢进快中快出慢
 */
class EaseCubicInterpolator : IEaseInterpolator {
    /**
     * 🚀 缓入（三次缓入）
     * 公式: f(x) = x³
     */
    override fun easeIn(): Interpolator {
        return Interpolator { x -> x * x * x }
    }

    /**
     * 🚀 缓出（三次缓出）
     * 公式: f(x) = 1 - (1 - x)³
     */
    override fun easeOut(): Interpolator {
        return Interpolator { x ->
            val t = 1f - x
            1f - t * t * t
        }
    }

    /**
     * 🚀 缓入缓出（三次缓入缓出）
     * 公式:
     *   if x < 0.5: 4x³
     *   else: 1 - ((-2x + 2)³ / 2)
     */
    override fun easeInOut(): Interpolator {
        return Interpolator { x ->
            if (x < 0.5f) {
                4f * x * x * x
            } else {
                val t = -2f * x + 2f
                1f - (t * t * t) / 2f
            }
        }
    }
}