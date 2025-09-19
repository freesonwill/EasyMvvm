package arch.cayenne.lib.common.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.CustomTabIndicator

object CustomTabIndicatorUtils  {

        private var currentAnimator: ValueAnimator? = null
        private val animationQueue: MutableList<Pair<Int, Boolean>> = mutableListOf()
        private var currentPosition: Int = 0 // 记录当前指示器位置

        fun animateIndicatorToPosition(customTabIndicator: CustomTabIndicator,position: Int, smoothScroll: Boolean = true) {
            // 将动画请求加入队列
            animationQueue.add(Pair(position, smoothScroll))

            // 如果没有动画在运行，处理队列
            if (currentAnimator == null || currentAnimator?.isRunning == false) {
                processNextAnimation(customTabIndicator)
            }
        }

        //当前位置,之前位置
        private fun processNextAnimation(customTabIndicator: CustomTabIndicator) {
            // 如果队列为空，清理并退出
            if (animationQueue.isEmpty()) {
                currentAnimator = null
                return
            }

            // 取出队列中的第一个请求
            val (targetPosition, smoothScroll) = animationQueue.removeAt(0)

            // 配置动画，从 currentPosition 到 targetPosition
            val duration: Long = if (smoothScroll) AnimationController[AnimType.scrollbar]?.duration ?: 200L else 0
            val interpolator: TimeInterpolator = AnimationController[AnimType.scrollbar]?.interpolator?.toInterpolator() ?: LinearInterpolator()
            currentAnimator = ValueAnimator.ofFloat(currentPosition.toFloat(), targetPosition.toFloat()).apply {
                this.duration = duration
                this.interpolator = interpolator
                addUpdateListener { animation ->
                    val progress = animation.animatedValue as Float
                    customTabIndicator.setIndicatorPosition(progress.toInt(), progress % 1f)
                }
                // 动画结束时更新 currentPosition 并处理下一个动画
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                      //  LogUtils.e("processNextAnimation-------currentPosition${currentPosition},targetPosition${targetPosition}")
                        currentPosition = targetPosition // 更新当前位置
                        processNextAnimation(customTabIndicator) // 处理下一个动画
                    }
                })
                // 启动动画
                start()
            }
        }

        //清理方法，用于组件销毁时
        fun clear() {
            currentAnimator?.cancel()
            animationQueue.clear()
            currentAnimator = null
            currentPosition = 0 // 重置当前位置
        }

}