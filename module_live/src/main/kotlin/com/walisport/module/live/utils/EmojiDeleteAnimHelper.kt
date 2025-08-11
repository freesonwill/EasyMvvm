package com.walisport.module.live.utils

import android.view.VelocityTracker
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlin.math.abs
import kotlin.math.max

/**
 * @author: wenxi
 * @date: 9/8/25 14:54
 * @description:
 */
class EmojiDeleteAnimHelper(private val recyclerView: RecyclerView) {
    private val layoutManager: GridLayoutManager by lazy {
        recyclerView.layoutManager as GridLayoutManager
    }
    private val spanCount: Int get() = layoutManager.spanCount
    private var velocityTracker: VelocityTracker? = null
    private val minVelocityForFastScroll = 2000f

    // 记录所有被强制修改过alpha的view及其原始alpha值
    private val modifiedViews = mutableMapOf<Int, Float>()

    init {
        setup()
    }

    private fun setup() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        velocityTracker = VelocityTracker.obtain()
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        velocityTracker?.recycle()
                        velocityTracker = null
                        ensureAllNonTargetItemsVisible()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTargetItemsVisibility()
            }
        })

        // 添加布局完成监听确保状态正确
        recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            ensureAllNonTargetItemsVisible()
        }
    }

    private fun updateTargetItemsVisibility() {
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION) return

        // 1. 计算当前需要隐藏的position（最后两行的最后两个item）
        val lastRowStart = lastVisible - (lastVisible % spanCount)
        val secondLastRowStart = max(firstVisible, lastRowStart - spanCount)

        val currentTargetPositions = mutableSetOf<Int>().apply {
            listOf(secondLastRowStart, lastRowStart).forEach { rowStart ->
                (spanCount - 2 until spanCount).forEach { offset ->
                    val pos = rowStart + offset
                    if (pos <= lastVisible) add(pos)
                }
            }
        }

        val isFastScrolling = isFastScrolling()

        // 2. 处理所有可见item
        (firstVisible..lastVisible).forEach { pos ->
            layoutManager.findViewByPosition(pos)?.let { view ->
                val shouldHide = pos in currentTargetPositions
                val currentAlpha = view.alpha

                when {
                    // 是需要隐藏的item且当前未隐藏
                    shouldHide && currentAlpha != 0f -> {
                        modifiedViews[pos] = currentAlpha // 保存原始alpha
                        applyAlpha(view, 0f, isFastScrolling)
                    }

                    // 不是目标item但被错误隐藏了
                    !shouldHide && currentAlpha != 1f -> {
                        // 恢复为记录的原始alpha或默认1f
                        val targetAlpha = modifiedViews.remove(pos) ?: 1f
                        applyAlpha(view, targetAlpha, isFastScrolling)
                    }
                }
            }
        }
    }

    private fun ensureAllNonTargetItemsVisible() {
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION) return

        // 强制恢复所有非目标item的显示状态
        (firstVisible..lastVisible).forEach { pos ->
            layoutManager.findViewByPosition(pos)?.let { view ->
                if (view.alpha != 1f && !isPositionInTargetArea(pos, firstVisible, lastVisible)) {
                    val targetAlpha = modifiedViews.remove(pos) ?: 1f
                    view.alpha = targetAlpha
                }
            }
        }
    }

    private fun isPositionInTargetArea(pos: Int, firstVisible: Int, lastVisible: Int): Boolean {
        val lastRowStart = lastVisible - (lastVisible % spanCount)
        val secondLastRowStart = max(firstVisible, lastRowStart - spanCount)

        return pos in listOf(secondLastRowStart, lastRowStart).flatMap { rowStart ->
            (spanCount - 2 until spanCount).map { rowStart + it }
        } && pos <= lastVisible
    }

    private fun isFastScrolling(): Boolean {
        return velocityTracker?.let {
            it.computeCurrentVelocity(1000)
            abs(it.yVelocity) > minVelocityForFastScroll
        } ?: false
    }

    private fun applyAlpha(view: View, targetAlpha: Float, fastScroll: Boolean) {
        if (fastScroll || view.alpha == targetAlpha) {
            view.alpha = targetAlpha
        } else {
            view.animate().cancel() // 取消可能存在的动画
            view.animate()
                .alpha(targetAlpha)
                .setDuration(200)
                .start()
        }
    }

    fun cleanup() {
        velocityTracker?.recycle()
        // 恢复所有被修改过的view
        modifiedViews.keys.forEach { pos ->
            layoutManager.findViewByPosition(pos)?.alpha = modifiedViews[pos] ?: 1f
        }
        modifiedViews.clear()
    }
}