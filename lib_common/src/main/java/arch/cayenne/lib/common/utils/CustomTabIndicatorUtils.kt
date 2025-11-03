package arch.cayenne.lib.common.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.AnimationController.AnimType
import arch.cayenne.lib.common.ui.view._interface.BaseCustomTabIndicator

object CustomTabIndicatorUtils {

    private var currentAnimator: ValueAnimator? = null
    private val animationQueue: MutableList<AnimationQueuePair> = mutableListOf()
    private var currentPosition = 0 // 记录当前指示器位置
    private var startTime  = 0L //2个数据之间的事件
    fun animateIndicatorToPosition(customTabIndicator: BaseCustomTabIndicator, position: Int, smoothScroll: Boolean = true) {
        var currentTime  =  System.currentTimeMillis()-startTime
        // 将最新的动画请求加入队列
        animationQueue.add(AnimationQueuePair(position, smoothScroll,currentTime))
        startTime = System.currentTimeMillis()
        // 如果没有动画在运行，处理队列
        if (currentAnimator == null || currentAnimator?.isRunning == false) {
            processNextAnimation(customTabIndicator)
        }else{
            if (animationQueue.size==1){
                currentAnimator?.cancel()
            }
        }
    }

    // 处理下一个动画
    private fun processNextAnimation(customTabIndicator: BaseCustomTabIndicator) {
        // 如果队列为空，清理并退出
        if (animationQueue.isEmpty()) {
            currentAnimator = null
            return
        }
        // 取出队列中的第一个数据
        val lastRequest = animationQueue.removeAt(0)
        val targetPosition = lastRequest.position
        val smoothScroll = lastRequest.smoothScroll
        // 配置动画，从 currentPosition 到 targetPosition
        val duration: Long = if (smoothScroll) AnimationController[AnimType.scrollbar]?.duration ?: 200L else 0
        val interpolator: TimeInterpolator = AnimationController[AnimType.scrollbar]?.interpolator?.toInterpolator() ?: LinearInterpolator()
        currentAnimator = ValueAnimator.ofFloat(currentPosition.toFloat(), targetPosition.toFloat()).apply {
            //是否在设定的时间内
            if (lastRequest.duration<duration){
                this.duration = lastRequest.duration
            }else{
                this.duration = duration
            }
            this.interpolator = interpolator
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                customTabIndicator.setIndicatorPosition(progress.toInt(), progress % 1f)
            }
            // 动画结束时更新 currentPosition 并处理下一个动画
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentPosition = targetPosition // 更新当前位置
                    processNextAnimation(customTabIndicator) // 处理下一个动画
                }
            })
            // 启动动画
            start()
        }
    }

    // 清理方法，用于组件销毁时
    fun clear() {
        currentAnimator?.cancel()
        animationQueue.clear()
        currentAnimator = null
        currentPosition = 0 // 重置当前位置
    }
}

data class AnimationQueuePair(val position: Int, val smoothScroll: Boolean,val duration: Long)