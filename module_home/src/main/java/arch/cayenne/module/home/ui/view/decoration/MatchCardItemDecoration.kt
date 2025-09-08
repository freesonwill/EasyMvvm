package arch.cayenne.module.home.ui.view.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class MatchCardItemDecoration(
    private val bottomSpacePx: Int,
    private val itemSpacePx: Int = 6.dp2px
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        outRect.bottom = bottomSpacePx
        if (position == 0) {
            outRect.top = itemSpacePx // 頂部不加下方間隔
        }
    }
}