package arch.cayenne.module.order.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OrderItemDecoration(private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // 獲取當前項目的位置

        if (position == 0) {
            outRect.top = space
            outRect.bottom = space
        } else {
            outRect.bottom = space
        }
    }
}