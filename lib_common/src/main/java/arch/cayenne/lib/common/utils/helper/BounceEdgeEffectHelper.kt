package arch.cayenne.lib.common.utils.helper

import android.content.Context
import android.graphics.Canvas
import android.view.animation.OvershootInterpolator
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class BounceEdgeEffectHelper(
    private val context: Context
) : RecyclerView.EdgeEffectFactory() {
    // 回彈效果與 BounceTabLayoutContainer 保持一致
    companion object {
        private const val MIN_PULL_DISTANCE = 0.005f
        private const val BOUNCE_DURATION = 400L
        private const val DAMPING_FACTOR = 0.5f
        private const val MAX_TRANSLATION_RATIO = 0.25f
        private const val OVERSHOOT_TENSION = 1.6f
        private const val PULL_SENSITIVITY = 1.2f
    }

    // 可配置的回彈參數
    var bounceDistanceRatio: Float = MAX_TRANSLATION_RATIO
        set(value) {
            field = value.coerceIn(0.1f, 0.5f)
        }

    var bounceDampingFactor: Float = DAMPING_FACTOR
        set(value) {
            field = value.coerceIn(0.3f, 0.8f)
        }

    var bounceDuration: Long = BOUNCE_DURATION
        set(value) {
            field = value.coerceIn(200L, 500L)
        }

    var overshootTension: Float = OVERSHOOT_TENSION
        set(value) {
            field = value.coerceIn(1.0f, 3.0f)
        }

    var pullSensitivity: Float = PULL_SENSITIVITY
        set(value) {
            field = value.coerceIn(0.8f, 2.0f)
        }

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(context) {
            private var isAnimating = false
            private var isPulling = false
            private var totalPullDistance = 0f
            private val maxTranslation = recyclerView.width * bounceDistanceRatio

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)

                // 如果正在動畫中，忽略所有拉動
                if (isAnimating) return

                val sign = if (direction == DIRECTION_LEFT) 1 else -1

                // 使用敏感度參數讓拉動更流暢
                totalPullDistance += kotlin.math.abs(deltaDistance) * pullSensitivity

                // 使用可配置的阻尼因子
                val targetTranslation =
                    sign * recyclerView.width * totalPullDistance * bounceDampingFactor
                val clampedTranslation = targetTranslation.coerceIn(-maxTranslation, maxTranslation)

                // 直接設置 translation
                recyclerView.translationX = clampedTranslation
                isPulling = true
            }

            override fun onRelease() {
                super.onRelease()

                // 如果沒有在拉動或正在動畫中，直接返回
                if (!isPulling || isAnimating) return

                // 如果拉動距離太小，直接重置
                if (totalPullDistance < MIN_PULL_DISTANCE) {
                    resetState()
                    return
                }

                // 開始回彈動畫
                startBounceAnimation()
            }

            override fun draw(canvas: Canvas?): Boolean = false

            private fun startBounceAnimation() {
                isAnimating = true
                isPulling = false

                recyclerView.animate()
                    .translationX(0f)
                    .setDuration(bounceDuration)
                    .setInterpolator(OvershootInterpolator(overshootTension))
                    .withEndAction {
                        resetState()
                    }
                    .start()
            }

            private fun resetState() {
                isAnimating = false
                isPulling = false
                totalPullDistance = 0f
                recyclerView.translationX = 0f
            }
        }
    }

    /**
     * 設置回彈效果參數
     */
    fun setBounceConfig(
        distanceRatio: Float? = null,
        dampingFactor: Float? = null,
        duration: Long? = null,
        tension: Float? = null,
        sensitivity: Float? = null
    ) {
        distanceRatio?.let { bounceDistanceRatio = it }
        dampingFactor?.let { bounceDampingFactor = it }
        duration?.let { bounceDuration = it }
        tension?.let { overshootTension = it }
        sensitivity?.let { pullSensitivity = it }
    }
}