package arch.cayenne.lib.common.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val horizontalSpacing: Int,
    var verticalSpacing: Int ,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        if (position == RecyclerView.NO_POSITION) {
            return
        }

        val layoutManager = parent.layoutManager as? GridLayoutManager
            ?: throw IllegalStateException("This ItemDecoration can only be used with a GridLayoutManager.")

        // 確保列數匹配
        if (layoutManager.spanCount != spanCount) {
            throw IllegalStateException("The spanCount of the GridLayoutManager must match the one set in the ItemDecoration.")
        }

        val column = position % spanCount // item column

        if (includeEdge) {
            // 這種算法會在最左和最右邊都留出空間
            outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount
            outRect.right = (column + 1) * horizontalSpacing / spanCount
            if (position < spanCount) { // top edge
                outRect.top = verticalSpacing
            }
            outRect.bottom = verticalSpacing // item bottom
        } else {
            // 這種算法確保最左和最右邊沒有空隙
            outRect.left = column * horizontalSpacing / spanCount
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount

            if (position >= spanCount) {
                outRect.top = verticalSpacing // non-top edge
            }
        }
    }
}