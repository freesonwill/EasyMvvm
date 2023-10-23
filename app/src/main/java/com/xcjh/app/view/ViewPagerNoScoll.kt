package com.xcjh.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class ViewPagerNoScoll : ViewPager {
    private var isCanScroll = false
    private var isHasScrollAnim = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    /**
     * 设置其是否能滑动
     * @param isCanScroll false 禁止滑动， true 可以滑动
     */
    fun setCanScroll(isCanScroll: Boolean) {
        this.isCanScroll = isCanScroll
    }

    /**
     * 设置是否去除滑动效果
     * @param isHasScrollAnim false 去除滚动效果， true 不去除
     */
    fun setHasScrollAnim(isHasScrollAnim: Boolean) {
        this.isHasScrollAnim = isHasScrollAnim
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isCanScroll && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isCanScroll && super.onTouchEvent(ev)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, smoothScroll)
    }

    /**
     * 设置其是否去求切换时的滚动动画
     * isHasScrollAnim为false时，会去除滚动效果
     */
    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, isHasScrollAnim)
    }
}