package com.walisport.module.hall.ui.view

import android.view.MotionEvent
import arch.cayenne.lib.skin.widget.SkinnableRecyclerView

class RankingRecyclerView : SkinnableRecyclerView {
    constructor(context: android.content.Context) : super(context)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet?) : super(context, attrs)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

override fun onMeasure(widthSpec: Int, heightSpec: Int) {
    val expandSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
    super.onMeasure(widthSpec, expandSpec)
}

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // 返回 false，不拦截事件，交给父 RecyclerView 处理
        return false
    }

}