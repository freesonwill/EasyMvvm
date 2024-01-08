package com.xcjh.base_lib.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.lihang.ShadowLayout
import com.xcjh.base_lib.R
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.bean.MsgTitle
import com.xcjh.base_lib.utils.indicator.CommonPagerIndicator
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView.OnPagerTitleChangeListener
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule
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


/**
 * viewPager2扩展方法
 */
fun ViewPager2.initFragment(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true//vp 是否可以左右滑动
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    setLm(this)
    isSaveEnabled = false
    return this
}

fun ViewPager2.initActivity(
    acivity: FragmentActivity,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true,//是否可滑动
    sensitive: Int = 2//设置灵敏度
): ViewPager2 {


    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(acivity) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
        override fun getItemId(position: Int): Long {
            return super.getItemId(position)
        }
    }
    setLm(this, sensitive)
    isSaveEnabled = false
    return this
}

/**
 * 设置灵敏度
 */
fun setLm(viewPager2: ViewPager2, sensitive: Int = 2) {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(viewPager2) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop * sensitive)
}


fun dip2px(dipValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dipValue * scale + .5f).toInt()
}


fun MagicIndicator.bindViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    unSelectSize: Float = 14f,
    typefaceBold: Boolean = false,//是否粗体
    scrollEnable: Boolean = false,//滚动
    lineIndicatorColor: Int = 0,//横线指示器
    lineIndicatorWidth: Int = 23,//横线指示器宽度
    smoothScroll: Boolean = true,//切换页面是否有滚动动画
    margin: Int = 15,   // 左右间距
    action: (index: Int) -> Unit = {}
) {
    //viewPager.offscreenPageLimit = mStringList.size
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

            val commonPagerTitleView = CommonPagerTitleView(context)
            val customLayout: View
            val titleText: TextView

            if (scrollEnable) {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout2, null)
                titleText = customLayout.findViewById(R.id.title_text)
                val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
                layoutParams.marginStart = appContext.dp2px(margin)
                layoutParams.marginEnd = appContext.dp2px(margin)
                titleText.layoutParams = layoutParams
            } else {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout, null)
                titleText = customLayout.findViewById(R.id.title_text)
            }

            // titleText.gravity=View.TEXT_ALIGNMENT_VIEW_START
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = unSelectSize
            titleText.gravity = Gravity.CENTER_VERTICAL

            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    // titleText.setTextColor(Color.WHITE)
                    titleText.textSize = selectSize
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    if (typefaceBold) {
                        //                       titleText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setTextBold(titleText, true)
                    }
                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.textSize = unSelectSize
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
//                    titleText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    if (typefaceBold) {
                        setTextBold(titleText, false)
                    }
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                action.invoke(index)
                viewPager.setCurrentItem(index, smoothScroll)
                // viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context): IPagerIndicator {
            if (lineIndicatorColor != 0) {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = dp2px(3).toFloat()
                indicator.lineWidth = dp2px(lineIndicatorWidth).toFloat()
                indicator.roundRadius = dp2px(40).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(ContextCompat.getColor(context, lineIndicatorColor))
                indicator.yOffset = dp2px(2).toFloat()
                return indicator
            } else {
                return CommonPagerIndicator(context).apply {
                    mode = 0
                    // indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_select)
                }
            }
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}

fun MagicIndicator.bindViewPager3(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    unSelectSize: Float = 14f,
    typefaceBold: Boolean = false,//是否粗体
    scrollEnable: Boolean = false,//滚动
    lineIndicatorColor: Int = 0,//横线指示器
    lineIndicatorWidth: Int = 23,//横线指示器宽度
    smoothScroll: Boolean = true,//切换页面是否有滚动动画
    margin: Int = 15,   // 左右间距
    action: (index: Int) -> Unit = {}
) {
    //viewPager.offscreenPageLimit = mStringList.size
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

            val commonPagerTitleView = CommonPagerTitleView(context)
            val customLayout: View
            val titleText: TextView

            if (scrollEnable) {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout2, null)
                titleText = customLayout.findViewById(R.id.title_text)
                if (index == (mStringList.size - 1)) {
                    val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
                    layoutParams.marginStart = appContext.dp2px(margin)
                    layoutParams.marginEnd = appContext.dp2px(margin + 10)
                    titleText.layoutParams = layoutParams
                } else {
                    val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
                    layoutParams.marginStart = appContext.dp2px(margin)
                    layoutParams.marginEnd = appContext.dp2px(margin)
                    titleText.layoutParams = layoutParams
                }

            } else {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout, null)
                titleText = customLayout.findViewById(R.id.title_text)
            }

            // titleText.gravity=View.TEXT_ALIGNMENT_VIEW_START
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = unSelectSize
            titleText.gravity = Gravity.CENTER_VERTICAL

            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    // titleText.setTextColor(Color.WHITE)
                    titleText.textSize = selectSize
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    if (typefaceBold) {
                        //                       titleText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setTextBold(titleText, true)
                    }
                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.textSize = unSelectSize
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
//                    titleText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    if (typefaceBold) {
                        setTextBold(titleText, false)
                    }
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                action.invoke(index)
                viewPager.setCurrentItem(index, smoothScroll)
                // viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context): IPagerIndicator {
            if (lineIndicatorColor != 0) {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = dp2px(3).toFloat()
                indicator.lineWidth = dp2px(lineIndicatorWidth).toFloat()
                indicator.roundRadius = dp2px(40).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(ContextCompat.getColor(context, lineIndicatorColor))
                indicator.yOffset = dp2px(2).toFloat()
                return indicator
            } else {
                return CommonPagerIndicator(context).apply {
                    mode = 0
                    // indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_select)
                }
            }
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}

/**
 * 文字+图标 指示器
 */
fun MagicIndicator.bindHomeViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),//文字
    icons: List<Int> = arrayListOf(),//图标
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    unSelectSize: Float = 14f,
    typefaceBold: Boolean = false,//是否粗体
    scrollEnable: Boolean = false,//滚动
    smoothScroll: Boolean = true,//切换页面是否有滚动动画
    margin: Int = 15,   // 左右间距
    action: (index: Int) -> Unit = {}
) {
    //viewPager.offscreenPageLimit = mStringList.size
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

            val commonPagerTitleView = CommonPagerTitleView(context)
            val titleText: TextView
            val titleImg: ImageView
            val customLayout: View =
                LayoutInflater.from(context).inflate(R.layout.home_tab_layout, null)
            titleImg = customLayout.findViewById(R.id.title_img)
            titleText = customLayout.findViewById(R.id.title_text)
            val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = appContext.dp2px(margin)
            layoutParams.marginEnd = appContext.dp2px(margin)
            // titleText.layoutParams = layoutParams

            // titleText.gravity=View.TEXT_ALIGNMENT_VIEW_START
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = unSelectSize
            titleText.gravity = Gravity.CENTER_VERTICAL
            titleImg.setImageResource(icons[index])

            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    // titleText.setTextColor(Color.WHITE)
                    titleText.textSize = selectSize
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    if (typefaceBold) {
                        //                       titleText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setTextBold(titleText, true)
                    }
                    titleImg.imageTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, selectColor))

                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.textSize = unSelectSize
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
//                    titleText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    if (typefaceBold) {
                        setTextBold(titleText, false)
                    }
                    titleImg.imageTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, normalColor))
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                action.invoke(index)
                viewPager.setCurrentItem(index, smoothScroll)
                // viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context): IPagerIndicator? {
            return null
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}


/**
 * 文字+图标 指示器 选择图片和未选择图片两种
 */
fun MagicIndicator.bindHomeSelectImageViewPager(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),//文字
    icons: List<Int> = arrayListOf(),//图标
    selectIcon: List<Int> = arrayListOf(),//选中图片
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    unSelectSize: Float = 14f,
    typefaceBold: Boolean = false,//是否粗体
    scrollEnable: Boolean = false,//滚动
    smoothScroll: Boolean = true,//切换页面是否有滚动动画
    margin: Int = 15,   // 左右间距
    action: (index: Int) -> Unit = {}
) {
    //viewPager.offscreenPageLimit = mStringList.size
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

            val commonPagerTitleView = CommonPagerTitleView(context)
            val titleText: TextView
            val titleImg: ImageView
            val customLayout: View =
                LayoutInflater.from(context).inflate(R.layout.home_tab_layout, null)
            titleImg = customLayout.findViewById(R.id.title_img)
            titleText = customLayout.findViewById(R.id.title_text)
            val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = appContext.dp2px(margin)
            layoutParams.marginEnd = appContext.dp2px(margin)
            // titleText.layoutParams = layoutParams

            // titleText.gravity=View.TEXT_ALIGNMENT_VIEW_START
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = unSelectSize
            titleText.gravity = Gravity.CENTER_VERTICAL
            titleImg.setImageResource(icons[index])

            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    // titleText.setTextColor(Color.WHITE)
                    titleText.textSize = selectSize
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    if (typefaceBold) {
                        //                       titleText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setTextBold(titleText, true)
                    }
                    titleImg.imageTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, selectColor))

                    titleImg.setImageResource(selectIcon[index])

                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.textSize = unSelectSize
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
//                    titleText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    if (typefaceBold) {
                        setTextBold(titleText, false)
                    }
                    titleImg.setImageResource(icons[index])
//                    titleImg.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, normalColor))
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                action.invoke(index)
                viewPager.setCurrentItem(index, smoothScroll)
                // viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context): IPagerIndicator? {
            return null
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}

/**
 * 比赛详情tab
 *
 */
fun MagicIndicator.bindMatchViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    unSelectSize: Float = 14f,
    typefaceBold: Boolean = false,//是否粗体
    scrollEnable: Boolean = false,//滚动
    lineIndicatorColor: Int = 0,//横线指示器
    smoothScroll: Boolean = true,//切换页面是否有滚动动画
    margin: Int = 15,   // 左右间距
    action: (index: Int) -> Unit = {}
) {
    //viewPager.offscreenPageLimit = mStringList.size
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

            val commonPagerTitleView = CommonPagerTitleView(context)
            val customLayout: View
            val titleText: TextView

            if (scrollEnable) {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout2, null)
                titleText = customLayout.findViewById(R.id.title_text)
                val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
                layoutParams.marginStart = appContext.dp2px(margin)
                layoutParams.marginEnd = appContext.dp2px(margin)
                titleText.layoutParams = layoutParams
            } else {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout, null)
                titleText = customLayout.findViewById(R.id.title_text)
            }

            // titleText.gravity=View.TEXT_ALIGNMENT_VIEW_START
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = unSelectSize
            titleText.gravity = Gravity.CENTER_VERTICAL

            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    // titleText.setTextColor(Color.WHITE)
                    titleText.textSize = selectSize
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    if (typefaceBold) {
                        //                       titleText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setTextBold(titleText, true)
                    }
                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.textSize = unSelectSize
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
//                    titleText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    if (typefaceBold) {
                        setTextBold(titleText, false)
                    }
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                action.invoke(index)
                viewPager.setCurrentItem(index, smoothScroll)
                // viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context): IPagerIndicator {
            if (lineIndicatorColor != 0) {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = dp2px(2).toFloat()
                indicator.lineWidth = dp2px(17).toFloat()
                indicator.roundRadius = dp2px(40).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(ContextCompat.getColor(context, lineIndicatorColor))
                indicator.yOffset = dp2px(0).toFloat()
                return indicator
            } else {
                return CommonPagerIndicator(context).apply {
                    mode = 0
                    // indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_select)
                }
            }
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}

/**
 *
 * 带内外框背景的指示器
 */
fun MagicIndicator.bindBgViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    unSelectSize: Float = 14f,
    typefaceBold: Boolean = false,//是否粗体
    scrollEnable: Boolean = false,//滚动

    marginStart: Int = 10,//tab左边距
    marginEnd: Int = 10,//tab右边距
    paddingH: Double = 9.0,//tab左右内边距
    paddingV: Double = 6.0,//tab上下内边距
    lineIndicatorColor: Int = 0,//横线指示器
    isLineIndicator: Boolean = true,//是否为普通滑动背景
    paddingWidth: Double = 0.0,//内边距
    action: (index: Int) -> Unit = {}
) {
    //viewPager.offscreenPageLimit = mStringList.size
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

            val commonPagerTitleView = CommonPagerTitleView(context)
            val customLayout: View
            val titleText: TextView

            if (scrollEnable) {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout2, null)
                titleText = customLayout.findViewById(R.id.title_text)
                val layoutParams = titleText.layoutParams as LinearLayout.LayoutParams
                layoutParams.marginStart = appContext.dp2px(marginStart)
                layoutParams.marginEnd = appContext.dp2px(marginEnd)
                if (!isLineIndicator) {
                    titleText.setPadding(
                        UIUtil.dip2px(context, paddingH),
                        UIUtil.dip2px(context, 6.0),
                        UIUtil.dip2px(context, paddingH),
                        UIUtil.dip2px(context, 6.0),
                    )
                }
                titleText.layoutParams = layoutParams
            } else {
                customLayout =
                    LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout, null)
                titleText = customLayout.findViewById(R.id.title_text)
                titleText.setPadding(
                    UIUtil.dip2px(context, paddingH),
                    UIUtil.dip2px(context, paddingV),
                    UIUtil.dip2px(context, paddingH),
                    UIUtil.dip2px(context, paddingV),
                )
            }

            // titleText.gravity=View.TEXT_ALIGNMENT_VIEW_START
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = unSelectSize
            titleText.gravity = Gravity.CENTER_VERTICAL

            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    titleText.textSize = selectSize
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    if (typefaceBold) {
                        setTextBold(titleText, true)
                    }
                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.textSize = unSelectSize
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
                    if (typefaceBold) {
                        setTextBold(titleText, false)
                    }
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                //onPageSelected(index)
                action.invoke(index)
                viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context): IPagerIndicator? {
            val indicator = LinePagerIndicator(context)
            val lineHeight = context.resources.getDimension(R.dimen.dp_30)
            val borderWidth = UIUtil.dip2px(context, paddingWidth).toFloat()
            indicator.lineHeight = lineHeight
            indicator.roundRadius = UIUtil.dip2px(context, 24.0).toFloat()
            indicator.yOffset = borderWidth
            indicator.setColors(ContextCompat.getColor(context, lineIndicatorColor))
            return if (isLineIndicator) {
                indicator
            } else {
                null
            }
        }

    }
    this.navigator = commonNavigator

    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}

/**
 *
 */
private fun setVpPageChangeCallBack(
    magicIndicator: MagicIndicator,
    viewPager: ViewPager2,
    action: (index: Int) -> Unit
) {
    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)

        }

        override fun onPageSelected(position: Int) {
            magicIndicator.onPageSelected(position)
            action.invoke(position)

        }

        override fun onPageScrollStateChanged(state: Int) {
            magicIndicator.onPageScrollStateChanged(state)

        }
    })
}


fun MagicIndicator.bindMatchDetailVP2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    selectColor: Int = R.color.selectColor,
    normalColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    scrollEnable: Boolean = false,//MagicIndicator false左右自适应
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
            val commonPagerTitleView = CommonPagerTitleView(context)

            // load custom layout
            val customLayout: View =
                LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout, null)
            val titleText = customLayout.findViewById<TextView>(R.id.title_text)
            titleText.gravity = View.TEXT_ALIGNMENT_CENTER
            titleText.text = mStringList[index].toHtml()
            titleText.textSize = selectSize
            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                override fun onSelected(index: Int, totalCount: Int) {
                    // titleText.setTextColor(Color.WHITE)
                    titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                    titleText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                }

                override fun onDeselected(index: Int, totalCount: Int) {
                    titleText.setTextColor(ContextCompat.getColor(context, normalColor))
                    titleText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
                }

                override fun onLeave(
                    index: Int,
                    totalCount: Int,
                    leavePercent: Float,
                    leftToRight: Boolean
                ) {
                }

                override fun onEnter(
                    index: Int,
                    totalCount: Int,
                    enterPercent: Float,
                    leftToRight: Boolean
                ) {
                }
            }

            commonPagerTitleView.setOnClickListener {
                action.invoke(index)
                viewPager.currentItem = index
            }

            return commonPagerTitleView

        }

        override fun getIndicator(context: Context?): IPagerIndicator? {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_EXACTLY
            indicator.lineHeight = UIUtil.dip2px(context, 2.0).toFloat()
            indicator.lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
            indicator.roundRadius = UIUtil.dip2px(context, 3.0).toFloat()
            indicator.startInterpolator = AccelerateInterpolator()
            indicator.endInterpolator = DecelerateInterpolator(2.0f)
            indicator.setColors(ContextCompat.getColor(context!!, selectColor))
            indicator.yOffset = UIUtil.dip2px(context, 3.0).toFloat()
            return indicator
        }

    }
    this.navigator = commonNavigator
    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this, viewPager, action)
}

/**
 *ViewPager2 + MagicIndicator 带小红点未读消息的
 */
fun MagicIndicator.bindViewBadgePager2(
    viewPager: ViewPager2,
    msgBean: List<MsgTitle> = arrayListOf(),
    selectColor: Int = R.color.selectColor,
    unSelectColor: Int = R.color.normalColor,
    selectSize: Float = 14f,
    scrollEnable: Boolean = false,
    hasLineIndicator: Int = R.color.selectColor,
    action: (index: Int) -> Unit = {}
) {
    //  viewPager.offscreenPageLimit = msgBean.size
    val commonNavigator = CommonNavigator(context)
    if (scrollEnable) {
        commonNavigator.isSkimOver = true
    } else {
        commonNavigator.isAdjustMode = true
    }
    commonNavigator.scrollPivotX = 0.25f
    commonNavigator.adapter = object : CommonNavigatorAdapter() {

        override fun getCount(): Int {
            return msgBean.size
        }

        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return BadgePagerTitleView(context).apply {
                innerPagerTitleView = CommonPagerTitleView(context).apply {

                    // load custom layout
                    val customLayout: View =
                        LayoutInflater.from(context).inflate(R.layout.live_tab_title_layout4, null)
                    val titleText = customLayout.findViewById<TextView>(R.id.title_text)
                    titleText.text = msgBean[index].name
                    titleText.textSize = selectSize
                    setContentView(customLayout)
                    onPagerTitleChangeListener = object : OnPagerTitleChangeListener {
                        override fun onSelected(index: Int, totalCount: Int) {
                            // titleText.setTextColor(Color.WHITE)
                            titleText.setTextColor(ContextCompat.getColor(context, selectColor))
                            setTextBold(titleText, true)
                        }

                        override fun onDeselected(index: Int, totalCount: Int) {
                            titleText.setTextColor(ContextCompat.getColor(context, unSelectColor))
                            setTextBold(titleText, false)
                        }

                        override fun onLeave(
                            index: Int,
                            totalCount: Int,
                            leavePercent: Float,
                            leftToRight: Boolean
                        ) {
                        }

                        override fun onEnter(
                            index: Int,
                            totalCount: Int,
                            enterPercent: Float,
                            leftToRight: Boolean
                        ) {
                        }
                    }
                    //点击事件
                    setOnClickListener {
                        //viewPager.currentItem = index
                        viewPager.setCurrentItem(index, true)
                        //badgeView = null
                        // action.invoke(index)
                    }
                }
                val badgeImageView = LayoutInflater.from(context)
                    .inflate(
                        com.xcjh.base_lib.R.layout.simple_red_dot_badge_layout,
                        null
                    ) as ImageView
                if (msgBean[index].msgNum > 0) {
                    badgeView = badgeImageView
                }
                //badgeView = badgeImageView
                xBadgeRule = BadgeRule(BadgeAnchor.CONTENT_RIGHT, -dip2px(15f))
                yBadgeRule = BadgeRule(BadgeAnchor.CONTENT_TOP, dip2px(5f))
                // setup badge
                /* if (index != 2) {
                     val badgeTextView = LayoutInflater.from(context).inflate(R.layout.simple_count_badge_layout, null) as TextView
                     badgeTextView.text = "" + (index + 1)
                     badgeView = badgeTextView
                 } else {

                 }*/

                // don't cancel badge when tab selected
                isAutoCancelBadge = false
            }
        }

        override fun getIndicator(context: Context): IPagerIndicator {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_EXACTLY
            indicator.lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
            indicator.lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
            indicator.roundRadius = UIUtil.dip2px(context, 2.0).toFloat()
            //indicator.startInterpolator = AccelerateInterpolator() //动画
            // indicator.endInterpolator = DecelerateInterpolator(2.0f)
            indicator.setColors(ContextCompat.getColor(context, hasLineIndicator))
            indicator.yOffset = UIUtil.dip2px(context, 1.0).toFloat()
            return indicator
        }

        override fun getTitleWeight(context: Context?, index: Int): Float {
            return if (index == 0) {
                1.6f
            } else if (index == 2) {
                1.6f
            } else {
                1.0f
            }
        }


    }
    this.navigator = commonNavigator
    //viewPager 绑定 navigator
    setVpPageChangeCallBack(this@bindViewBadgePager2, viewPager, action)
}

/*
 * ViewPager + MagicIndicator 指示器
 */
fun MagicIndicator.bindViewPager(
    viewPager: ViewPager,
    mStringList: List<String> = arrayListOf(),
    action: (index: Int) -> Unit = {},
    scrollEnable: Boolean = false,
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
            return ColorTransitionPagerTitleView(context).apply {
                //设置文本
                text = mStringList[index].toHtml()
                //字体大小
                textSize = 22f
                setTextBold(this, true)
                // setBackgroundColor(ContextCompat.getColor(appContext, R.color.red_F7736D))
                //未选中颜色
                normalColor = ContextCompat.getColor(context, R.color.normalColor)
                //选中颜色
                selectedColor = ContextCompat.getColor(context, R.color.selectColor)
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
 * 为TabLayout添加分割线
 */
fun TabLayout.addDivider(resId: Int, padding: Float = 14f): TabLayout {
    val linearLayout = this.getChildAt(0) as LinearLayout
    linearLayout.apply {
        dividerDrawable = ContextCompat.getDrawable(this.context, resId)
        showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        //dividerPadding = com.luck.picture.lib.tools.ScreenUtils.dip2px(this.context, padding)
    }

    return this
}

/**
 * TabLayout绑定vg
 */
fun TabLayout.initTabLayout(
    tabs: ArrayList<String>,
    viewPager2: ViewPager2,
    action: (index: Int) -> Unit = {}
) {
    removeAllTabs()
    tabs.forEach { name ->
        val tab = newTab()
        tab.text = name
        addTab(tab)
    }
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                viewPager2.setCurrentItem(tab.position, false)
                selectTab(getTabAt(it.position))
                action.invoke(tab.position)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    })
    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            selectTab(getTabAt(position))
            action.invoke(position)
        }
    })
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

/**
 * 解决Gson转换导致int转换成double的问题
 */
fun getGsonStr(num: Double): String {
    val format = DecimalFormat("0.########")
    //format.roundingMode = RoundingMode.FLOOR
    return format.format(num)
}

/**
 * 获取 h5 标签中的内容
 */
fun getH5Content(htmlStr: String): String {
    val regFormat = "\\s*|\t|\r|\n"
    val regTag = "<[^>]*>"
    return htmlStr.replace(regFormat.toRegex(), "").replace(regTag.toRegex(), "")
}

/**
 * 获取 h5 标签中的内容
 * 保留原始 空格\t、回车\r、换行符\n、制表符\t
 */
fun getH5Content2(htmlStr: String): String {
    val regTag = "<[^>]*>"
    return htmlStr.replace(regTag.toRegex(), "")
}

/**
 * 处理loadDataWithBaseURL 加载换行
 */
fun getH5Content3(htmlStr: String): String {
    val regFormat = "\n"
    return htmlStr.replace(regFormat.toRegex(), "<br>")
}

/**
 * webView 设置缩放后，文本适配屏幕自动换行
 *       settings.setSupportZoom(true)  settings.builtInZoomControls = true
 */
fun webViewBreak(data: String): String {
    var data1 = data
    if (data.length > 7 && data.contains("<p>") && data.contains("</p>")) {
        data1 = data.replace("<p>".toRegex(), "<p style=\"word-break:break-all\">")
    }
    return data1
}