package com.walisport.module.live.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class LeagueItemDecoration(
    private val isHeader: (position: Int) -> Boolean,
    private val createHeaderView: (Context, ViewGroup) -> View,
    private val bindHeaderView: (headerView: View, position: Int) -> Unit
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return
        val headerPos = findCurrentHeaderPosition(topChildPosition)
        if (headerPos == -1) return
        val context = parent.context
        val headerView = createHeaderView(context, parent)
        bindHeaderView(headerView, headerPos)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        headerView.measure(widthSpec, heightSpec)
        headerView.layout(
            0,
            2.dp2px,
            parent.width,
            parent.paddingTop + headerView.measuredHeight
        )
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