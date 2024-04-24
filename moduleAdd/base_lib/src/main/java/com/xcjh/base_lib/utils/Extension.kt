package com.xcjh.base_lib.utils

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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.xcjh.base_lib.R 
import com.xcjh.base_lib.utils.indicator.CommonPagerIndicator
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
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

fun MagicIndicator.bindViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    selectColor: Int = R.color.normalColor,
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
                layoutParams.marginStart = context.dp2px(margin)
                layoutParams.marginEnd = context.dp2px(margin)
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
            //点击增加透明度
//            commonPagerTitleView.setOnTouchListener { view, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        titleText.alpha=0.3f
//                    }
//                      MotionEvent.ACTION_UP->{
//                          // 处理抬起事件
//                          titleText.alpha=1f
//                      }
//
//
//                }
//
//                false
//            }
            commonPagerTitleView.setContentView(customLayout)
            commonPagerTitleView.onPagerTitleChangeListener = object :
                CommonPagerTitleView.OnPagerTitleChangeListener {
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