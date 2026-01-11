package arch.cayenne.lib.common.utils.ext

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.scrollToBottomWithLoadMore(
    minScrollCount: Int = 4,
    onEndAction: () -> Unit,
    onBottomAction: (() -> Unit)? = null,
) {
    val layoutManager = this.layoutManager as LinearLayoutManager?
    val lastItemPos = layoutManager!!.findLastCompletelyVisibleItemPosition()
    val itemCount = this.adapter!!.itemCount - minScrollCount
    if (lastItemPos > itemCount && lastItemPos > 1) {
        onEndAction()
    }
    if (lastItemPos == this.adapter!!.itemCount - 1) {
        onBottomAction?.invoke()
    }
}

fun RecyclerView.listenAtTop(onTopChanged: (Boolean) -> Unit) {
    // Track last top state to avoid duplicate callbacks
    var lastIsAtTop: Boolean? = null

    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            checkTopState(onTopChanged, lastIsAtTop) { newState ->
                lastIsAtTop = newState
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // Check once when scrolling stops
                checkTopState(onTopChanged, lastIsAtTop) { newState ->
                    lastIsAtTop = newState
                }
                // Delayed check to ensure layout stability
                post {
                    checkTopState(onTopChanged, lastIsAtTop) { newState ->
                        lastIsAtTop = newState
                    }
                }
            }
        }

        private fun checkTopState(
            onTopChanged: (Boolean) -> Unit,
            lastIsAtTop: Boolean?,
            updateLastState: (Boolean) -> Unit
        ) {
            val layoutManager = layoutManager as? LinearLayoutManager
            // Check if first item is fully or partially visible
            val isAtTop = layoutManager?.let {
                val firstVisiblePosition = it.findFirstVisibleItemPosition()
                firstVisiblePosition == 0 && computeVerticalScrollOffset() == 0
            } ?: false

            // Trigger callback only when state changes
            if (isAtTop != lastIsAtTop) {
                onTopChanged(isAtTop)
                updateLastState(isAtTop)
            }
        }
    })
}

/**
 * 检查当前滚动状态，并触发相应的回调
 *
 * @param thresholdDown
 * @param thresholdTop
 * @param onDowScrolling
 * @param onTopScrolling
 */
fun RecyclerView.addScrollThresholdListener(
    thresholdDown: Int,     // 向下滚多远触发（隐藏）
    thresholdTop: Int,      // 回到顶部多近触发（显示）
    onDowScrolling: () -> Unit,
    onTopScrolling: () -> Unit
) {
    var totalScrolledPx = computeVerticalScrollOffset().toFloat()
    when {
        // 向下滚动：距离顶部超过 thresholdDowDp → 触发隐藏
        totalScrolledPx >= thresholdDown -> {
            onDowScrolling.invoke()
        }

        // 向上滚动：回到顶部附近（小于 thresholdTopDp）→ 触发显示
        totalScrolledPx <= thresholdTop -> {
            onTopScrolling.invoke()
        }
    }

    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            // 计算当前已经滚动的距离（从顶部开始累计）
            totalScrolledPx = rv.computeVerticalScrollOffset().toFloat()

            when {
                // 向下滚动：距离顶部超过 thresholdDowDp → 触发隐藏
                totalScrolledPx >= thresholdDown -> {
                    onDowScrolling.invoke()
                }

                // 向上滚动：回到顶部附近（小于 thresholdTopDp）→ 触发显示
                totalScrolledPx <= thresholdTop -> {
                    onTopScrolling.invoke()
                }
            }
        }
    })
}

/**
 * 检查当前滚动状态，并触发相应的回调
 *
 * @param thresholdDowDp
 * @param thresholdTopDp
 * @param onDowScrolling
 * @param onTopScrolling
 */
 private fun RecyclerView.checkCurrentScrollState(
    thresholdDowDp: Float = 100f,     // 向下滚多远触发（隐藏）
    thresholdTopDp: Float = 80f,      // 回到顶部多近触发（显示）
    onDowScrolling: () -> Unit,
    onTopScrolling: () -> Unit
) {
    val totalScrolledPx = computeVerticalScrollOffset().toFloat()
    val thresholdDownPx = thresholdDowDp * resources.displayMetrics.density
    val thresholdTopPx = thresholdTopDp * resources.displayMetrics.density
    // 计算当前已经滚动的距离（从顶部开始累计）
    when {
        // 向下滚动：距离顶部超过 thresholdDowDp → 触发隐藏
        totalScrolledPx >= thresholdDownPx -> {
            onDowScrolling.invoke()
        }

        // 向上滚动：回到顶部附近（小于 thresholdTopDp）→ 触发显示
        totalScrolledPx <= thresholdTopPx -> {
            onTopScrolling.invoke()
        }
    }
}
