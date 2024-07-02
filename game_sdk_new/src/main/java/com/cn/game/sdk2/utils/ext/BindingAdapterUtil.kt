package com.cn.game.sdk2.utils.ext

import android.content.Context
import android.util.Log
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.R
import com.cn.game.sdk2.ui.helper.ViewHelper.setTextBold
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.view.game.GameAreaView.LocationClickListener
import com.cn.game.sdk2.utils.tool.indicator.CommonPagerIndicator
import com.cn.game.sdk2.websocket.bean.Betting
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
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
 * createTime   : 2024/6/24 15:04
 **/
object BindingAdapterUtil {
    const val TAG = "BindingAdapterUtil"

    @BindingAdapter("areaInfo")
    @JvmStatic
    fun setAreaInfo(view: GameAreaView, areaInfo: Betting) {
        view.areaInfo = areaInfo
    }

    @BindingAdapter("moneyOKClickListener")
    @JvmStatic
    fun setMoneyOKClickListener(view: GameAreaView, listener: MoneyOKView.OnMoneyOKClickListener) {
        view.moneyView.setMoneyOKClickListener(listener)
    }

    @BindingAdapter("OnLocationClickListener")
    @JvmStatic
    fun setOnLocationClickListener(view: GameAreaView, listener: LocationClickListener) {
        view.setOnLocationClickListener(listener)
    }


    @JvmStatic
    @BindingAdapter(value = ["dividerSpace", "dividerOrientation"], requireAll = false)
    fun dividerSpace(recyclerView: RecyclerView, space: Int, orientation: Int?) {
        val or = when (orientation) {
            0 -> DividerOrientation.HORIZONTAL
            1 -> DividerOrientation.VERTICAL
            3 -> DividerOrientation.GRID
            else -> DividerOrientation.HORIZONTAL
        }
        recyclerView.dividerSpace(space, or)
    }

    @JvmStatic
    @BindingAdapter("layout_marginBottom")
    fun setLayoutMarginBottom(view: ConstraintLayout, margin: Int) {
        val lp = view.layoutParams as  RelativeLayout.LayoutParams
        lp.bottomMargin = margin
        view.layoutParams = lp
    }

    fun MagicIndicator.bindRecycleView(
        recyclerView: RecyclerView,
        mStringList: List<String> = arrayListOf(),
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