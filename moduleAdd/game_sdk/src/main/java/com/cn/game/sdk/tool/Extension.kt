package com.cn.game.sdk.tool

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.cn.game.sdk.R
import com.cn.game.sdk.tool.indicator.CommonPagerIndicator
import com.cn.game.sdk.view.ClickTextView
import com.cn.game.sdk.view.CombinationOkView
import com.google.android.material.tabs.TabLayout
import com.xcjh.base_lib.utils.toHtml
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 该文件只添加扩展方法，其他top函数根据业务情况合理安置，便于查找、管理
 * 各种公共扩展方法
 */

fun ViewPager.init(
    fragmentManager: FragmentManager,
    fragments: ArrayList<Fragment>,
    titles: ArrayList<String>? = null
): ViewPager {
    //设置适配器
    adapter = object : FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles?.get(position)
        }

    }

    return this
}

















/*
 * ViewPager + MagicIndicator 指示器
 */
fun MagicIndicator.bindViewPagerNew(
    viewPager: ViewPager,
    mStringList: List<String> = arrayListOf(),
    scrollEnable: Boolean = false,
    action: (index: Int) -> Unit = {}
) {
    // viewPager.offscreenPageLimit = mStringList.size
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
//                setOnTouchListener(View.OnTouchListener { v, event ->
//                    if (v is CombinationOkView) {
//                    Log.i("VVVVVVVVV","1111111111111")
//
//                    }else{
//                        Log.i("VVVVVVVVV","22222222222222")
//                    }
//
//                    return@OnTouchListener false
//                })
                //设置文本
                text = mStringList[index].toHtml()
                //字体大小
                textSize = 14f
                setTextBold(this, true)
                // setBackgroundColor(ContextCompat.getColor(appContext, R.color.red_F7736D))
                //未选中颜色
                normalColor = ContextCompat.getColor(context, R.color.g_9696b8)
                //选中颜色
                selectedColor = ContextCompat.getColor(context, R.color.g_f7cf41)
                //点击事件
                setOnClickListener {
                    viewPager.currentItem = index
                    action.invoke(index)
                }




            }
        }

        override fun getIndicator(context: Context): IPagerIndicator {
            return CommonPagerIndicator(context).apply {
                mode = 0
                // indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_select)
            }
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    ViewPagerHelper.bind(this, viewPager)
}






/**
 * 文字加粗无效的时候，如： textView.setTypeface(null, Typeface.BOLD) 或者 textView.typeface = Typeface.DEFAULT_BOLD
 */
fun setTextBold(textView: TextView?, isBold: Boolean) {
    try {
        if (textView != null) {
            val paint: Paint? = textView.paint
            paint?.isFakeBoldText = isBold
        }
    } catch (_: Exception) {
    }
}


/**
 * 最多保留两位小数
 * 3.341 -> 3.34
 * 3.349 -> 3.34
 *
 * 3.40 -> 3.4
 * 3.0 -> 3
 */
fun getNoMoreThanTwoDigits(num: Double): String {
    val format = DecimalFormat("0.##")
    format.roundingMode = RoundingMode.FLOOR
    return format.format(num)
}
