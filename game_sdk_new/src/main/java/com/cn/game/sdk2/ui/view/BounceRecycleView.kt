package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xcjh.base_lib2.utils.LogUtils


open class BounceRecycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    init {
        edgeEffectFactory = BounceEdgeEffectFactory()
    }
    private var mThumbHeight: Float = UNDEFINED

    // Where the RecyclerView cuts off the views when the RecyclerView is scrolled to top.
    // For example, if 1/4 of the view at position 9 is displayed at the bottom of the RecyclerView,
    // mTopCutOff will equal 9.25. This value is used to compute the scroll offset.
    private var mTopCutoff: Float = UNDEFINED

    companion object {
        private val ITEM_HEIGHT: Int = 1000 // Arbitrary, make largish for smoother scrolling
        private val UNDEFINED: Float = -1f
        private val ERROR_NOT_AT_TOP_OF_RANGE: String = "RecyclerView must be positioned at the top of its range."
        // The size of the scroll bar thumb in our units.
        private const val TAG = "BounceRecycleView"
    }

    override fun computeVerticalScrollRange(): Int {
        LogUtils.dTag(TAG,"computeVerticalScrollRange,${mTopCutoff},ITEM_HEIGHT:${mThumbHeight}")
        if (mThumbHeight == UNDEFINED) {
            val lm = layoutManager as LinearLayoutManager
            val firstCompletePosition = lm.findFirstVisibleItemPosition()

            if (firstCompletePosition != NO_POSITION) {
                if (firstCompletePosition != 0) {
                    throw (IllegalStateException(ERROR_NOT_AT_TOP_OF_RANGE))
                } else {
                    mTopCutoff = getCutoff()
                    mThumbHeight = (mTopCutoff * ITEM_HEIGHT)
                }
            }
        }
        return adapter!!.itemCount * ITEM_HEIGHT
    }

    override fun computeVerticalScrollOffset(): Int {
        //LogUtils.dTag(TAG,"computeVerticalScrollOffset,${mTopCutoff},ITEM_HEIGHT:${mThumbHeight}")
        return if ((mTopCutoff == UNDEFINED)) 0 else ((getCutoff() - mTopCutoff) * ITEM_HEIGHT).toInt()
    }

    override fun computeVerticalScrollExtent(): Int {
        //LogUtils.dTag(TAG,"computeVerticalScrollExtent,${mTopCutoff},ITEM_HEIGHT:${mThumbHeight}")
        return if ((mThumbHeight == UNDEFINED)) 0 else mThumbHeight.toInt()
    }

    private fun  getCutoff(): Float {
        val lm = layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = lm.findLastVisibleItemPosition()
        if (lastVisibleItemPosition == NO_POSITION) {
            return 0f
        }
        val view = lm.findViewByPosition(lastVisibleItemPosition)

        val fractionOfView = if (view!!.bottom < height) { // last visible position is fully visible
            0f
        } else { // last view is cut off and partially displayed
            (height - view.top).toFloat() / view.height.toFloat()
        }
        return lastVisibleItemPosition + fractionOfView
    }

}