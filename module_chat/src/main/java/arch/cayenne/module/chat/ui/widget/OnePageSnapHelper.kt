package arch.cayenne.module.chat.ui.widget

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @author: wenxi
 * @date: 26/9/25 15:25
 * @description:
 */
class OnePageSnapHelper : PagerSnapHelper() {

    private var recyclerView: RecyclerView? = null
    private var startX = 0f
    private var lastX = 0f

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        this.recyclerView = recyclerView


    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        val currentPosition = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
        val itemCount = layoutManager.itemCount

        // 计算滑动距离
        val scrollDistance = abs(lastX - startX)
        val screenWidth = recyclerView?.width ?: 0

        // 根据滑动距离比例决定跳转页数
        val distanceRatio = scrollDistance / screenWidth
        val jumpSize = calculateJumpByDistance(distanceRatio, velocityX)

        val targetPosition = if (velocityX > 0 || lastX < startX) {
            minOf(currentPosition + jumpSize, itemCount - 1)
        } else {
            maxOf(currentPosition - jumpSize, 0)
        }

        return targetPosition
    }

    private fun calculateJumpByDistance(distanceRatio: Float, velocityX: Int): Int {
        return when {
            distanceRatio > 2.0 -> 8 // 滑动距离超过2屏：跳8页
            distanceRatio > 1.5 -> 5 // 滑动1.5屏：跳5页
            distanceRatio > 1.0 -> 3 // 滑动1屏：跳3页
            distanceRatio > 0.5 -> 2 // 滑动半屏：跳2页
            else -> 1                // 小距离滑动：跳1页
        }
    }

}