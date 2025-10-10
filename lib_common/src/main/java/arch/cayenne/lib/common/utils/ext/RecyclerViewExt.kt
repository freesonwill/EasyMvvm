package arch.cayenne.lib.common.utils.ext

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi

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
