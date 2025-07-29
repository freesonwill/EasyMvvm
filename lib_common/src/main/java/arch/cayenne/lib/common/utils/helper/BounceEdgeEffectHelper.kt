package arch.cayenne.lib.common.utils.helper

import android.content.Context
import android.graphics.Canvas
import android.view.animation.DecelerateInterpolator
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class BounceEdgeEffectHelper(
    private val context: Context
) : RecyclerView.EdgeEffectFactory() {

    companion object {
        private const val MIN_PULL_DISTANCE = 0.005f
        private const val BOUNCE_DURATION = 250L
        private const val DAMPING_FACTOR = 0.6f
        private const val MAX_TRANSLATION_RATIO = 0.15f
    }

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(context) {
            private var isAnimating = false
            private var isPulling = false
            private var totalPullDistance = 0f
            private val maxTranslation = recyclerView.width * MAX_TRANSLATION_RATIO

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)

                // 如果正在動畫中，忽略所有拉動
                if (isAnimating) return

                val sign = if (direction == DIRECTION_LEFT) 1 else -1

                // 累加拉動距離（使用絕對值避免抖動）
                totalPullDistance += kotlin.math.abs(deltaDistance)

                // 計算 translation
                val targetTranslation =
                    sign * recyclerView.width * totalPullDistance * DAMPING_FACTOR
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
                    .setDuration(BOUNCE_DURATION)
                    .setInterpolator(DecelerateInterpolator())
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
}