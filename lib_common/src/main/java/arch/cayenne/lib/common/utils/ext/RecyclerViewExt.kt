package arch.cayenne.lib.common.utils.ext

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.scrollToBottomWithLoadMore(
    onEndAction: () -> Unit
) {
    val layoutManager = this.layoutManager as LinearLayoutManager?
    val lastItemPos = layoutManager!!.findLastCompletelyVisibleItemPosition()
    val itemCount = this.adapter!!.itemCount - 4
    if (lastItemPos > itemCount && lastItemPos > 1) {
        onEndAction()
    }
}