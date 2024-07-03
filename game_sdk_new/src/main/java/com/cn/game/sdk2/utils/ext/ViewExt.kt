package com.cn.game.sdk2.utils.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.ui.helper.ViewHelper.setTextBold
import com.cn.game.sdk2.utils.tool.indicator.CommonPagerIndicator
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.fragment.BaseVmFragment
import com.xcjh.base_lib.utils.toHtml
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/20 10:59
 **/
object ViewExt {

    inline val View.locationOnScreen:IntArray get(){
        val pos = IntArray(2)
        this.getLocationOnScreen(pos)
        return pos
    }

    inline val View.locationInWindow:IntArray get(){
        val pos = IntArray(2)
        this.getLocationInWindow(pos)
        return pos
    }

    inline val View.locationInSurface:IntArray  @RequiresApi(Build.VERSION_CODES.Q) get(){
        val pos = IntArray(2)
        this.getLocationInSurface(pos)
        return pos
    }

    //是否在View区域内
    fun View.isInArea(rawX:Float,rawY:Float):Boolean{
        val rawXY = IntArray(2)
        getLocationOnScreen(rawXY)
        return rawX >= rawXY[0] && rawX <= (rawXY[0] + width) && rawY >= rawXY[1] && rawY <= (rawXY[1] + height)
    }

    fun BaseVmFragment<*>.getDrawable(@DrawableRes id:Int):Drawable{
        return  ContextCompat.getDrawable(requireContext(),id)!!
    }

    @ColorInt fun BaseVmFragment<*>.getColor(@ColorRes id:Int): Int {
        return  ContextCompat.getColor(requireContext(),id)
    }


    fun MagicIndicator.bindRecycleView(
        recyclerView: RecyclerView,
        mStringList: Array<String> = arrayOf(),
        scrollEnable: Boolean = false,
        action: (index: Int) -> Unit = {}
    ) {
        val commonNavigator = CommonNavigator(context)
        if (scrollEnable) {
            commonNavigator.isSkimOver = true
        } else {
            commonNavigator.isAdjustMode = true
        }
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mStringList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                requestDisallowInterceptTouchEvent(true)

                return ColorTransitionPagerTitleView(context).apply {
                    //设置文本
                    text = mStringList[index].toHtml()
                    //字体大小
                    textSize = 14f
                    setTextBold(this, true)
                    //未选中颜色
                    normalColor = ContextCompat.getColor(context, R.color.c_8F9095)
                    //选中颜色
                    selectedColor = ContextCompat.getColor(context, R.color.c_3994F9)
                    //点击事件
                    setOnClickListener {
                        action.invoke(index)
                        recyclerView.stopScroll()
                        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index,0)
                    }
                }
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                return CommonPagerIndicator(context).apply {
                    mode = CommonPagerIndicator.MODE_MATCH_EDGE
                }
            }
        }
        this.navigator = commonNavigator
        val indicator = this
        //viewPager 绑定 navigator
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position: Int = layoutManager.findFirstVisibleItemPosition()
                //Log.d(TAG,"position-->$position,dx:$dx,dy:$dy")
                indicator.onPageSelected(position)
                indicator.onPageScrolled(position,0f,0)
            }

            // newState 表示滚动状态：
            // SCROLL_STATE_IDLE（停止滚动）
            // SCROLL_STATE_DRAGGING（拖动中）
            // SCROLL_STATE_SETTLING（滚动后停止）
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //Log.d(TAG,"position-->newState:$newState")
            }
        })
    }
}