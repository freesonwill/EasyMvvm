package com.xcjh.app.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.appbar.AppBarLayout
import com.xcjh.app.R
import com.xcjh.app.adapter.ViewPager2Adapter
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.TabBean
import com.xcjh.app.ui.details.fragment.DetailAnchorFragment
import com.xcjh.app.ui.details.fragment.DetailChatFragment
import com.xcjh.app.ui.details.fragment.DetailIndexFragment
import com.xcjh.app.ui.details.fragment.DetailLineUpFragment
import com.xcjh.app.ui.details.fragment.DetailLiveFragment
import com.xcjh.app.ui.details.fragment.DetailResultFragment
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.view.visibleOrGone
import net.lucode.hackware.magicindicator.MagicIndicator


fun setUnScroll(lltFold: ViewGroup) {
    val params = lltFold.layoutParams as AppBarLayout.LayoutParams
    //设置不能滑动
    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
    //以下代码是让layout_scrollFlags的控件获取焦点的，如果不设置，可能会有问题；
    lltFold.isFocusable = true
    lltFold.isFocusableInTouchMode = true
    lltFold.requestFocus()
    lltFold.layoutParams = params
}
//SCROLL_FLAG_SNAP 会就近惯性折叠伸展
fun setScroll(lltFold: ViewGroup) {
    //重新设置布局可以滑动
    val params = lltFold.layoutParams as AppBarLayout.LayoutParams
    params.scrollFlags =
        (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)
    lltFold.layoutParams = params
}

/**
 * 比赛状态解析
 */
fun getMatchStatusStr(matchType: String, status: Int): String {
    if ("1" == matchType) {//1：足球；2：篮球
        //足球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;
        //1 未开赛;2 上半场;3 中场;4 下半场;5 加时赛;6 加时赛(弃用);7 点球决战;8 完场;9 推迟;10 中断;11 腰斩;12 取消;13 待定
        return when (status) {
            0 -> "比赛异常"//此处可以隐藏处理，看UI设计
            1 -> "未开赛"
            2 -> "上半场"
            3 -> "中场"
            4 -> "下半场"
            5 -> "加时赛"
            6 -> "加时赛"
            7 -> "点球决战"
            8 -> "已结束"
            9 -> "推迟"
            10 -> "中断"
            11 -> "腰斩"
            12 -> "取消"
            13 -> "待定"
            else -> "比赛异常"
        }
    } else {
        //篮球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;
        // 2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定
        return when (status) {
            0 -> "比赛异常"//此处可以隐藏处理，看UI设计
            1 -> "未开赛"
            in 2..9 -> "比赛中"
           /* 2 -> "第一节"
            3 -> "第一节完"
            4 -> "第二节"
            5 -> "第二节完"
            6 -> "第三节"
            7 -> "第三节完"
            8 -> "第四节"
            9 -> "加时"*/
            10 -> "已结束"
            11 -> "中断"
            12 -> "取消"
            13 -> "延期"
            14 -> "腰斩"
            15 -> "待定"
            else -> "比赛异常"
        }
    }
}

/**
 * 比赛状态
 */
fun getMatchStatus(textView: TextView, matchType: String, status: Int) {
    textView.text = getMatchStatusStr(matchType, status)
    if ("1" == matchType) {//1：足球；2：篮球
        //足球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;
        //1 未开赛;2 上半场;3 中场;4 下半场;5 加时赛;6 加时赛(弃用);7 点球决战;8 完场;9 推迟;10 中断;11 腰斩;12 取消;13 待定
        when (status) {
            2, 3, 4, 5, 6, 7 -> textView.setTextColor(appContext.getColor(com.xcjh.base_lib.R.color.white))
            8 -> textView.setTextColor(appContext.getColor(R.color.c_F7DA73))
            else -> textView.setTextColor(appContext.getColor(R.color.c_8a91a0))
        }
    } else {
        //篮球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;
        // 2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定
        when (status) {
            2, 3, 4, 5, 6, 7, 8, 9 -> textView.setTextColor(appContext.getColor(com.xcjh.base_lib.R.color.white))
            10 -> textView.setTextColor(appContext.getColor(R.color.c_F7DA73))
            else -> textView.setTextColor(appContext.getColor(R.color.c_8a91a0))
        }
    }
}

fun setMatchStatusTime(
    tvTime: TextView,
    tvTimeS: TextView,
    matchType: String,
    status: Int,
    runTime: Int,
) {
    if (matchType == "1") {
        if (status in 2..7) {
            tvTime.text = runTime.toString()
            tvTimeS.text = " '"
            tvTime.visibleOrGone(false)
            tvTimeS.visibleOrGone(true)
        } else {
            tvTime.visibleOrGone(false)
            tvTimeS.visibleOrGone(false)
        }
    } else {
        if (status in 2..9) {
            tvTime.text = runTime.toString()
            tvTimeS.text = " '"
            tvTime.visibleOrGone(false)
            tvTimeS.visibleOrGone(true)
        } else {
            tvTime.visibleOrGone(false)
            tvTimeS.visibleOrGone(false)
        }
    }
}
private val tabs by lazy {
    arrayListOf(
        TabBean(1, name = appContext.resources.getStringArray(R.array.str_football_detail_tab)[0]),
        TabBean(3, name = appContext.resources.getStringArray(R.array.str_football_detail_tab)[2]),
        TabBean(4, name = appContext.resources.getStringArray(R.array.str_football_detail_tab)[3]),
        TabBean(5, name = appContext.resources.getStringArray(R.array.str_football_detail_tab)[4]),
        TabBean(2, name = appContext.resources.getStringArray(R.array.str_football_detail_tab)[1]),
        TabBean(6, name = appContext.resources.getStringArray(R.array.str_football_detail_tab)[5]),
    )
}
/**
 * 设置新的Tab+Vp
 */
fun setNewViewPager(
    signalPos: Int,
    mTitles: ArrayList<String>,
    mFragList: ArrayList<Fragment>,
    isHasAnchor: Boolean,
    anchorId: String?,
    detailBean: MatchDetailBean,
    pager2Adapter: ViewPager2Adapter,
    viewPager: ViewPager2,
    magicIndicator: MagicIndicator,
) {
    mTitles.clear()
    mFragList.clear()
    val newTabs = arrayListOf<TabBean>()
    newTabs.addAll(tabs)
    val iterator = newTabs.iterator()
    for (tab in iterator) {
        if (!isHasAnchor) { // 主播tab 隐藏
            if (tab.type == 2) {
                iterator.remove()
            }
            /*  if (tab.type == 6) {
                iterator.remove()
            }*/
        }
        if (!detailBean.matchData.hasStata) { // 赛况 隐藏
            if (tab.type == 3) {
                iterator.remove()
            }
        }
        if (!detailBean.matchData.hasLineup) { // 阵容 隐藏
            if (tab.type == 4) {
                iterator.remove()
            }
        }
        if (detailBean.matchType == "1") {
            if (!detailBean.matchData.hasOdds) { // 指数 隐藏
                if (tab.type == 5) {
                    iterator.remove()
                }
            }
        } else {
            //篮球没有指数
            if (tab.type == 5) {
                iterator.remove()
            }
        }
    }
    val liveId = detailBean.anchorList?.get(signalPos)?.liveId
        ?: "${detailBean.matchType}${detailBean.matchId}"
    newTabs.forEach { t ->
        mTitles.add(t.name)
        when (t.type) {
            1 -> mFragList.add(DetailChatFragment(liveId, anchorId))
            2 -> mFragList.add(DetailAnchorFragment(anchorId ?: ""))
            3 -> mFragList.add(DetailResultFragment(detailBean))//赛况
            4 -> mFragList.add(DetailLineUpFragment(detailBean))//阵容
            5 -> mFragList.add(DetailIndexFragment(detailBean.matchId, detailBean.matchType))//指数
            6 -> mFragList.add(DetailLiveFragment(liveId, detailBean.matchType))
        }
    }
    //重新更新viewpager2
    pager2Adapter.update(mFragList)
    viewPager.offscreenPageLimit = mFragList.size
    viewPager.adapter?.notifyDataSetChanged()
    magicIndicator.navigator.notifyDataSetChanged()
}
fun handleSoftInput(context: Activity,layout: RelativeLayout) {
    var currentHeight = 0
    val layoutParams = layout.layoutParams as RelativeLayout.LayoutParams
    val decorView = context.window.decorView
    decorView.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        decorView.getWindowVisibleDisplayFrame(rect)
        val keyboardMinHeight = SizeUtils.dp2px(100f)
        val screenHeight =
            if (hasNavigationBar(decorView)) context.resources.displayMetrics.heightPixels else decorView.height
        val rectHeight = if (hasNavigationBar(decorView)) rect.height() else rect.bottom
        val heightDiff = screenHeight - rectHeight
        // 视图树变化高度大于100dp,认为键盘弹出
        // currentHeight 防止界面频繁刷新，降低帧率，耗电
        if (currentHeight != heightDiff && heightDiff > keyboardMinHeight) {
            // 键盘弹出
            currentHeight = heightDiff
            layoutParams.bottomMargin = currentHeight
            layout.requestLayout()

        } else if (currentHeight != heightDiff && heightDiff < keyboardMinHeight) {
            // 键盘收起
            currentHeight = 0
            layoutParams.bottomMargin = currentHeight
            layout.requestLayout()

        }

    }
}
fun hasNavigationBar(view: View): Boolean {
    val compact = ViewCompat.getRootWindowInsets(view.findViewById(android.R.id.content))
    compact?.apply {
        return isVisible(WindowInsetsCompat.Type.navigationBars()) && getInsets(
            WindowInsetsCompat.Type.navigationBars()
        ).bottom > 0
    }
    return false
}