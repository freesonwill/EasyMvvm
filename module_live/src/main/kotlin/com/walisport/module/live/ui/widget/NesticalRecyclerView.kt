package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class NesticalRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(false)
        return false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }
}