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
        "KC_ lastItemPos = ${lastItemPos} itemCount = $itemCount".logi()
        onEndAction()
    }
    if (lastItemPos == this.adapter!!.itemCount - 1) {
        onBottomAction?.invoke()
    }
}