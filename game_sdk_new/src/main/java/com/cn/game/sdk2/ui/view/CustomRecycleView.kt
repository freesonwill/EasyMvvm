package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px


class CustomRecycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private var mThumbHeight: Int = UNDEFINED
    private var mTopCutoff:Float = UNDEFINED.toFloat()

/*
    *//**
     * Retrieves the size of the scroll bar thumb in our arbitrary units.
     *
     * @return Scroll bar thumb height
     *//*
    override fun computeVerticalScrollExtent(): Int {
        //return if ((mThumbHeight == UNDEFINED)) 0 else mThumbHeight
        return 200.dp2px
    }

    *//**
     * Compute the offset of the scroll bar thumb in our scroll bar range.
     *
     * @return Offset in scroll bar range.
     *//*
    override fun computeVerticalScrollOffset(): Int {
        //return if ((mTopCutoff == UNDEFINED.toFloat())) 0 else ((getCutoff() - mTopCutoff) * ITEM_HEIGHT).toInt()
        return (super.computeVerticalScrollOffset() / 23.5f).toInt();
    }

    *//**
     * Computes the scroll bar range. It will simply be the number of items in the adapter
     * multiplied by the given item height. The scroll extent size is also computed since it
     * will not vary. Note: The RecyclerView must be positioned at the top or this method
     * will throw an IllegalStateException.
     *
     * @return The scroll bar range
     *//*
    override fun computeVerticalScrollRange(): Int {
        return 5000
        *//*if (mThumbHeight == UNDEFINED) {
            val lm = layoutManager as LinearLayoutManager
            val firstCompletePosition = lm.findFirstCompletelyVisibleItemPosition()

            if (firstCompletePosition != NO_POSITION) {
                if (firstCompletePosition != 0) {
                    throw (IllegalStateException(ERROR_NOT_AT_TOP_OF_RANGE))
                } else {
                    mTopCutoff = getCutoff()
                    mThumbHeight = (mTopCutoff * ITEM_HEIGHT).toInt()
                }
            }
        }
        return adapter!!.itemCount * ITEM_HEIGHT*//*
    }

    private fun getCutoff(): Float {
        val lm = layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
        if (lastVisibleItemPosition == NO_POSITION) {
            return 0f
        }
        val view = lm.findViewByPosition(lastVisibleItemPosition)!!
        val fractionOfView = if (view.bottom < height) { // last visible position is fully visible
            0f
        } else { // last view is cut off and partially displayed
            (height - view.top).toFloat() / view.height.toFloat()
        }
        return lastVisibleItemPosition + fractionOfView
    }*/

    companion object {
        const val ITEM_HEIGHT: Int = 1000 // Arbitrary, make largish for smoother scrolling
        const val UNDEFINED: Int = -1
        const val ERROR_NOT_AT_TOP_OF_RANGE: String = "RecyclerView must be positioned at the top of its range."
    }
}