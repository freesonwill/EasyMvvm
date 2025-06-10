package arch.cayenne.module.home.ui.view.decoration

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val createHeaderView: () -> View,
    private val bindHeaderView: (headerView: View, position: Int) -> Unit
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return

        val headerPos = findCurrentHeaderPosition(topChildPosition)
        if (headerPos == -1) return

        val headerView = createHeaderView()
        bindHeaderView(headerView, headerPos)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        headerView.measure(widthSpec, heightSpec)
        headerView.layout(0, 0, headerView.measuredWidth, headerView.measuredHeight)

        // 計算是否被下一個 header 推上來
        val contactPoint = headerView.bottom
        val childInContact = getChildInContact(parent, contactPoint)
        val childPos = childInContact?.let { parent.getChildAdapterPosition(it) } ?: -1

        var offset = 0f
        if (childPos != -1 && isHeader(childPos) && childPos > headerPos) {
            val overlap = childInContact!!.top - headerView.height
            if (overlap < 0) {
                offset = overlap.toFloat()
            }
        }

        c.save()
        c.translate(0f, offset)
        headerView.draw(c)
        c.restore()
    }

    private fun findCurrentHeaderPosition(from: Int): Int {
        for (position in from downTo 0) {
            if (isHeader(position)) return position
        }
        return -1
    }

    private fun getChildInContact(parent: RecyclerView, contactY: Int): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.top <= contactY && child.bottom >= contactY) {
                return child
            }
        }
        return null
    }
}
