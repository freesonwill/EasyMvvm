package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class CustomRecycleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when(e.action) {
            MotionEvent.ACTION_DOWN->{

            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE ->{

            }
        }
        return super.onTouchEvent(e)
    }
}