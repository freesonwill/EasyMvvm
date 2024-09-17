package com.cn.game.sdk2.utils.ext

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.cn.game.sdk2.R
import com.cn.game.sdk2.utils.tool.indicator.CommonPagerIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.xcjh.base_lib2.utils.toHtml
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

fun ViewPager.initGameViewPager2(views: ArrayList<View>): ViewPager {
    //设置适配器
    adapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return views.count()
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = views[position]
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
    return this
}

fun ViewPager.initGameViewPager(
    fragmentManager: FragmentManager,
    fragments: List<Fragment>,
    titles: List<String>? = null
): ViewPager {
    //设置适配器
    adapter = object :
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        var cacheMap = hashMapOf<Int, Fragment>()
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        /*override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = getItem(position)
            if(!cacheMap.containsKey(position)){
                cacheMap[position] = fragment
                val transaction = fragmentManager.beginTransaction()
                transaction.add(
                  x  container.id, fragment,
                    "android:switcher:" + container.id + ":" + getItemId(position)
                )
                transaction.commitNowAllowingStateLoss()
            }

            return fragment
        }*/

        override fun getPageTitle(position: Int): CharSequence? {
            return titles?.get(position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            //super.destroyItem(container, position, `object`)
            //                fragments.remove(`object`)
        }
    }
    return this
}


/**
 * 该文件只添加扩展方法，其他top函数根据业务情况合理安置，便于查找、管理
 * 各种公共扩展方法
 */

fun ViewPager.initActivityGame(
    fragmentManager: FragmentManager,
    fragments: ArrayList<Fragment>,
    titles: ArrayList<String>? = null
): ViewPager {
    //设置适配器
    adapter = object : FragmentStatePagerAdapter(
        fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
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
fun MagicIndicator.bindViewPagerNewGame(
    viewPager: ViewPager,
    mStringList: List<String> = arrayListOf(),
    scrollEnable: Boolean = false,
    action: (index: Int) -> Unit = {}
) {
    // viewPager.offscreenPageLimit = mStringList.size
    val commonNavigator = CommonNavigator(context)
//    if (scrollEnable) {
//        commonNavigator.isSkimOver = true
//    } else {
//        commonNavigator.isAdjustMode = true
//    }
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
                this.setTextBold(false)
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
                setPadding(32, 0, 32, 0)
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

fun TabLayout.bindTabNewGame(
    viewPager: ViewPager2,
    titles: List<String> = arrayListOf(),
    scrollEnable: Boolean = false,
    action: (index: Int) -> Unit = {}
) {
    this.tabMode = if (scrollEnable) TabLayout.MODE_SCROLLABLE else TabLayout.MODE_FIXED
    var tabClickedByUser = false
    TabLayoutMediator(this, viewPager) { tab, position ->
        val tabView = tab.view
        if (position < titles.size) {
            tab.text = titles[position]
        }
        if (position == 0) tabView.setPadding(0, 0, 32, 0) else tabView.setPadding(
            32,
            0,
            32,
            0
        )
        tabView.setOnClickListener {
            tabClickedByUser = true
        }
    }.attach()

    this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            val position = tab?.position ?: 0
            viewPager.currentItem = position
            if (tabClickedByUser) {
                action.invoke(position)
                tabClickedByUser = false // 重置点击状态
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    })
}

fun TabLayout.removeTips() {
    for (i in 0 until tabCount) {
        getTabAt(i)?.view?.let { tabView ->
            TooltipCompat.setTooltipText(tabView, null)
        }
    }
}

/***
 * 设置ViewPager2的overScroll模式和回弹效果
 * @param overScrollMode 回弹模式
 * @param effectFactory  回弹效果工厂
 */
@JvmOverloads
fun ViewPager2.setOverScrollModeExt(overScrollMode: Int,effectFactory: RecyclerView.EdgeEffectFactory? = null) {
    val view = getChildAt(0)
    if (view is RecyclerView) {
        view.overScrollMode = overScrollMode
        if(effectFactory != null) view.edgeEffectFactory = effectFactory
    }
}

/***
 * 设置ViewPager2的overScroll模式和回弹效果
 * @param overScrollMode 回弹模式
 * @param orientation  Either ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
 */
@JvmOverloads
fun ViewPager2.setOverScrollModeExt(overScrollMode: Int,orientation:Int) {
    val view = getChildAt(0)
    if (view is RecyclerView) {
        view.overScrollMode = overScrollMode
        OverScrollDecoratorHelper.setUpOverScroll(view,orientation);
    }
}
