package com.cn.game.sdk.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager

class RelativeLayoutNew (context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // 在此处添加你的逻辑来判断是否拦截触摸事件
        // 如果需要将事件传递给 Fragment 中的 View，则返回 false
        // 如果不需要传递事件，则返回 true（即拦截事件）

        // 例如，你可以根据触摸事件的坐标位置来决定是否拦截事件
        // 这里是一个简单的示例，假设当触摸事件发生在 ViewPager 区域之外时不拦截事件
       Log.i("SSSSSSSSSSSS","2222222222222")
        return false
        // 默认情况下，继续执行 ViewPager 的默认逻辑
        return super.onInterceptTouchEvent(event)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
         return false
        return super.onTouchEvent(event)
    }
}