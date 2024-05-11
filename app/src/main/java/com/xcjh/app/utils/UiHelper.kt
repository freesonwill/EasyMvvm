package com.xcjh.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.setup
import com.drake.engine.base.app
import com.google.android.material.appbar.AppBarLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebParentLayout
import com.just.agentweb.WebViewClient
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.app.R
import com.xcjh.app.adapter.ViewPager2Adapter
import com.xcjh.app.bean.FirstMsgBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.NoticeBean
import com.xcjh.app.bean.TabBean
import com.xcjh.app.databinding.ItemDetailChatBinding
import com.xcjh.app.databinding.ItemDetailChatFirstBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.fragment.DetailAnchorFragment
import com.xcjh.app.ui.details.fragment.DetailChat2Fragment
import com.xcjh.app.ui.details.fragment.DetailIndexFragment
import com.xcjh.app.ui.details.fragment.DetailLineUpFragment
import com.xcjh.app.ui.details.fragment.DetailLiveFragment
import com.xcjh.app.ui.details.fragment.DetailResultBasketFragment
import com.xcjh.app.ui.details.fragment.DetailResultFootballFragment
import com.xcjh.app.view.PopupKickOut
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.SpanUtil
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.setTextBold
import com.xcjh.base_lib.utils.toHtml
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
            0 -> "${appContext.getString(R.string.competition_txt_abnormal)}"   //此处可以隐藏处理，看UI设计
            1 -> "${appContext.getString(R.string.main_txt_wks)}"
            2 -> "${appContext.getString(R.string.competition_txt_the)}"//"上半场"
            3 -> "${appContext.getString(R.string.zc)}"
            4 -> "${appContext.getString(R.string.competition_txt_the)}"//"下半场"
            5 -> "${appContext.getString(R.string.competition_txt_extra)}"
            6 -> "${appContext.getString(R.string.competition_txt_extra)}"
            7 -> "${appContext.getString(R.string.main_dqdz)}"
            8 -> "${appContext.getString(R.string.main_txt_over)}"
            9 -> "${appContext.getString(R.string.main_txt_tc)}"
            10 -> "${appContext.getString(R.string.main_txt_zd)}"
            11 -> "${appContext.getString(R.string.main_txt_yz)}"
            12 -> "${appContext.getString(R.string.main_txt_qx)}"
            13 -> "${appContext.getString(R.string.main_txt_dd)}"
            else -> "${appContext.getString(R.string.competition_txt_abnormal)}"
        }
    } else {
        //篮球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;
        // 2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定
        return when (status) {
            0 -> "${appContext.getString(R.string.competition_txt_abnormal)}"//此处可以隐藏处理，看UI设计
            1 -> "${appContext.getString(R.string.main_txt_wks)}"
            in 2..9 -> "${appContext.getString(R.string.competition_txt_the)}"
            /* 2 -> "第一节"
             3 -> "第一节完"
             4 -> "第二节"
             5 -> "第二节完"
             6 -> "第三节"
             7 -> "第三节完"
             8 -> "第四节"
             9 -> "加时"*/
            10 -> "${appContext.getString(R.string.main_txt_over)}"
            11 -> "${appContext.getString(R.string.main_txt_zd)}"
            12 -> "${appContext.getString(R.string.main_txt_qx)}"
            13 -> "${appContext.getString(R.string.main_txt_yq)}"
            14 -> "${appContext.getString(R.string.main_txt_yz)}"
            15 -> "${appContext.getString(R.string.main_txt_dd)}"
            else -> "${appContext.getString(R.string.competition_txt_abnormal)}"
        }
    }
}

/**
 * 比赛状态解析首页
 */
fun getMatchStatusStrMain(matchType: String, status: Int,context: Context): String {
    if ("1" == matchType) {//1：足球；2：篮球
        //足球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;
        //1 未开赛;2 上半场;3 中场;4 下半场;5 加时赛;6 加时赛(弃用);7 点球决战;8 完场;9 推迟;10 中断;11 腰斩;12 取消;13 待定
        return when (status) {
            0 -> "${appContext.getString(R.string.competition_txt_abnormal)}"   //此处可以隐藏处理，看UI设计
            1 -> "${appContext.getString(R.string.main_txt_wks)}"
            2 -> "${appContext.getString(R.string.recommend_txt_sbc)}"//"上半场"
            3 -> "${appContext.getString(R.string.zc)}"
            4 -> "${appContext.getString(R.string.recommend_txt_xbc)}"//"下半场"
            5 -> "${appContext.getString(R.string.competition_txt_extra)}"
            6 -> "${appContext.getString(R.string.competition_txt_extra)}"
            7 -> "${appContext.getString(R.string.main_dqdz)}"
            8 -> "${appContext.getString(R.string.main_txt_over)}"
            9 -> "${appContext.getString(R.string.main_txt_tc)}"
            10 -> "${appContext.getString(R.string.main_txt_zd)}"
            11 -> "${appContext.getString(R.string.main_txt_yz)}"
            12 -> "${appContext.getString(R.string.main_txt_qx)}"
            13 -> "${appContext.getString(R.string.main_txt_dd)}"
            else -> "${appContext.getString(R.string.competition_txt_abnormal)}"
        }
    } else {
        //篮球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;
        // 2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定
        return when (status) {
            0 -> "${appContext.getString(R.string.competition_txt_abnormal)}"//此处可以隐藏处理，看UI设计
            1 -> "${appContext.getString(R.string.main_txt_wks)}"
             2 -> context.getString(R.string.first_txt_one)
             3 -> context.getString(R.string.first_txt_one_end)
             4 -> context.getString(R.string.first_txt_two)
             5 ->  context.getString(R.string.first_txt_two_end)
             6 -> context.getString(R.string.first_txt_three)
             7 ->  context.getString(R.string.first_txt_three_end)
             8 -> context.getString(R.string.first_txt_four)
             9 -> context.getString(R.string.over_time)
            10 -> "${appContext.getString(R.string.main_txt_over)}"
            11 -> "${appContext.getString(R.string.main_txt_zd)}"
            12 -> "${appContext.getString(R.string.main_txt_qx)}"
            13 -> "${appContext.getString(R.string.main_txt_yq)}"
            14 -> "${appContext.getString(R.string.main_txt_yz)}"
            15 -> "${appContext.getString(R.string.main_txt_dd)}"
            else -> "${appContext.getString(R.string.competition_txt_abnormal)}"
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
            2, 3, 4, 5, 6, 7 -> textView.setTextColor(appContext.getColor(R.color.c_87_white))
            8 -> textView.setTextColor(appContext.getColor(R.color.c_87_white))
            else -> textView.setTextColor(appContext.getColor(R.color.c_87_white))
        }
    } else {
        //篮球状态码：0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;
        // 2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定
        when (status) {
            2, 3, 4, 5, 6, 7, 8, 9 -> textView.setTextColor(appContext.getColor(R.color.c_87_white))
            10 -> textView.setTextColor(appContext.getColor(R.color.c_87_white))
            else -> textView.setTextColor(appContext.getColor(R.color.c_87_white))
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
            tvTime.text = when (status) {
                5 -> "${appContext.getString(R.string.home_txt_overtime)}"
                7 -> "${appContext.getString(R.string.competition_txt_penalty)}"
                else -> runTime.toString()
            }
            tvTimeS.text = " '"
            tvTime.visibleOrGone(true)
            tvTimeS.visibleOrGone(true)
        } else {
            tvTimeS.visibleOrGone(false)
        }
    } else {
        if (status in 2..9) {
            tvTime.text = when (status) {
                2 -> "${appContext.getString(R.string.first_txt_one)}"
                3 -> "${appContext.getString(R.string.first_txt_one_end)}"
                4 -> "${appContext.getString(R.string.first_txt_two)}"
                5 -> "${appContext.getString(R.string.first_txt_two_end)}"
                6 -> "${appContext.getString(R.string.first_txt_three)}"
                7 -> "${appContext.getString(R.string.first_txt_three_end)}"
                8 -> "${appContext.getString(R.string.first_txt_four)}"
                9 -> "${appContext.getString(R.string.over_time)}"
                else -> "${appContext.getString(R.string.competition_txt_abnormal)}"
            }
            tvTimeS.text = " '"
            tvTime.visibleOrGone(true)
            tvTimeS.visibleOrGone(true)
        } else {
            // tvTime.visibleOrGone(false)
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
    mTitles: ArrayList<String>,
    mFragList: ArrayList<Fragment>,
    isHasAnchor: Boolean,
    anchorId: String?,
    detailBean: MatchDetailBean,
    pager2Adapter: ViewPager2Adapter,
    viewPager: ViewPager2,
    magicIndicator: MagicIndicator,
    context: Context
) {
    mTitles.clear()
    mFragList.clear()
    val newTabs = arrayListOf<TabBean>()

  var   tabsNew=arrayListOf(
        TabBean(1, name = context.resources.getStringArray(R.array.str_football_detail_tab)[0]),
        TabBean(3, name = context.resources.getStringArray(R.array.str_football_detail_tab)[2]),
        TabBean(4, name = context.resources.getStringArray(R.array.str_football_detail_tab)[3]),
        TabBean(5, name = context.resources.getStringArray(R.array.str_football_detail_tab)[4]),
        TabBean(2, name = context.resources.getStringArray(R.array.str_football_detail_tab)[1]),
        TabBean(6, name = context.resources.getStringArray(R.array.str_football_detail_tab)[5]),
    )
    newTabs.addAll(tabsNew)
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
    var liveId = ""
    detailBean.anchorList?.forEach {
        if (it.isSelect) {
            liveId =
                if (it.liveId.isNullOrEmpty()) "${detailBean.matchType}${detailBean.matchId}" else it.liveId
        }
    }
    newTabs.forEach { t ->
        mTitles.add(t.name)
        when (t.type) {
            //1 -> mFragList.add(DetailChatFragment(liveId, anchorId))
            1 -> mFragList.add(DetailChat2Fragment(liveId, anchorId))
            2 -> mFragList.add(DetailAnchorFragment(anchorId ?: ""))
            3 -> if ( detailBean.matchType=="1"){
                mFragList.add(DetailResultFootballFragment(detailBean))
            } else{
                mFragList.add(DetailResultBasketFragment(detailBean))
            }
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

fun handleSoftInput(context: Activity, layout: RelativeLayout) {
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

fun setProgressValue(progress: Int): Int {
    var p = progress
    if (progress < 20) {
        p = 20
    } else if (progress > 80) {
        p = 80
    }
    return p
}

/**
 * 聊天列表
 * @isReverse 是否翻转 默认不
 */
@SuppressLint("ClickableViewAccessibility")
fun setChatRoomRcv(
    vm: DetailVm,
    rcvChat: RecyclerView,
    mLayoutManager: HoverLinearLayoutManager,
    isReverse: Boolean = false,
    action: (AgentWeb) -> Unit = {},
    finishLoad: (Boolean) -> Unit = {},
    offset: (String?) -> Unit = {},
) {
    rcvChat.setHasFixedSize(true)
    rcvChat.apply {
        layoutManager = mLayoutManager
    }.setup {
        lateinit var mAgentWeb: AgentWeb
        addType<NoticeBean> {
            R.layout.item_detail_chat_notice // 公告
        }
        addType<FirstMsgBean> {
            R.layout.item_detail_chat_first // 首条消息
        }
        addType<MsgBean> {
            R.layout.item_detail_chat // 我发的消息
        }
        onCreate {
            when (itemViewType) {
                R.layout.item_detail_chat_first -> {

                    val binding = getBinding<ItemDetailChatFirstBinding>()
                    mAgentWeb = agentWeb(rcvChat.context, binding.agentWeb, finishLoad)
                    action.invoke(mAgentWeb)
                }
            }
        }
        onBind {
            when (val item = _data) {
                is NoticeBean -> {

                }

                is FirstMsgBean -> {

                    val binding = getBinding<ItemDetailChatFirstBinding>()

                    if( models!!.size==1&&layoutPosition==0){
                        val layoutParams =
                            binding.lltItem.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.setMargins(0, context.dp2px(10),0 , 0)
                        binding.lltItem.layoutParams = layoutParams
                    }else{
                        val layoutParams =
                            binding.lltItem.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.setMargins(0, context.dp2px(0),0 , 0)
                        binding.lltItem.layoutParams = layoutParams
                    }
                    //主播加入超链接
                    /* "<font color=\"#34A853\" font-weight=\"500\">${item.nick} : </font>${item.content}".toHtml {
                         Handler(Looper.getMainLooper()).post {
                             binding.tvContent.text = it
                         }
                     }*/
                    /* val bb = item.content.replaceFirst("<p>", "<span>")
                         .replace("</p>", "</span>")*/
                    val aa =
                        "<p><span style='color: #34A853;font-weight:500;margin-right:5px;flex-shrink:0;'>jr680036:</span>测试测试出色测试测试出色测</p><p><span style='color: #34A853;font-weight:500;margin-right:5px;flex-shrink:0;'>jr680036:</span> <a href=\\\"https://www.baidu.com/\\\" target=\\\"_blank\\\">https://www.baidu.com/</a> </p><p><span style='color: #34A853;font-weight:500;margin-right:5px;flex-shrink:0;'>jr680036:</span><span style=\\\"color: rgb(225, 60, 57);\\\">测试测试出色</span></p>"
                    val bb =
                        "<html><head><style>* {font-size: 14px;color: #ffffff;font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(item.content)}</body></html>"
                    // binding.webView.loadData(aa+javascript,"text/html", "UTF-8") //aa.toHtml()
                  //  mAgentWeb.urlLoader.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)
//                    if(!item.content.equals("")){
//                        binding.lltItem.visibility=View.VISIBLE
//
//                    }else{
//                        binding.lltItem.visibility=View.GONE
//                    }
//                    setH5Data(mAgentWeb.webCreator.webView,item.content, tvColor ="#ffffff", maxLine =2 )
                    setH5DataMaxLine(mAgentWeb.webCreator.webView,item.content, tvColor ="#ffffff")
                }

                is MsgBean -> {
                    val binding = getBinding<ItemDetailChatBinding>()
                    setTextBold(binding.tvLevel, item.identityType != 0)
                      if(item.msgType==6){
                        binding.llDetailShow.visibility=View.GONE
                        binding.txtSystem.visibility=View.VISIBLE
                        binding.txtSystem.text= item.content
                    }else if (item.identityType == 0||item.identityType == 3) {
                        binding.llDetailShow.visibility=View.VISIBLE
                        binding.txtSystem.visibility=View.GONE
                        binding.ivImage.visibleOrGone(false)
                        binding.ivLevel.visibleOrGone(true)
                        binding.lltLevel.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.c_1AFFFFFF)
                        )
                        binding.ivLevel.setImageResource(setLeverDrawable(item.level))
                        binding.tvLevel.text = getLeverNum(item.level)
                    }else {
                        binding.llDetailShow.visibility=View.VISIBLE
                        binding.txtSystem.visibility=View.GONE
                        binding.tvLevel.text = app.getString(R.string.anchor)
                        binding.lltLevel.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.c_3334A853)
                        )
                        binding.ivLevel.visibleOrGone(false)
                        binding.ivImage.visibleOrGone(item.msgType == 1)
                        if (item.msgType == 1) {//图片
                            Glide.with(context)
                                .load(item.content)
                                .placeholder(R.drawable.load_square)
                                .into(binding.ivImage)
                            binding.ivImage.setOnClickListener {
                                val list = arrayListOf<LocalMedia>()
                                val localMedia = LocalMedia()
                                localMedia.path = item.content
                                localMedia.cutPath = item.content
                                list.add(localMedia)
                                PictureSelector.create(context)
                                    .openPreview()
                                    .setImageEngine(GlideEngine.createGlideEngine())
                                    .startActivityPreview(0, false, list)
                            }
                        }
                    }
                    if (isReverse) {
                        if (modelPosition + 1 == models?.size) {
                            offset.invoke(item.id ?: "")
                            item.id?.loge("====+++++++++")
                        }
                    } else {
                        if (modelPosition == 0) {
                            offset.invoke(item.id ?: "")
                        }
                    }
                    val section = SpanUtil.create()
                        .addForeColorSection(
                            item.nick + " : ",
                            ContextCompat.getColor(context, R.color.c_94999f)
                        )
                        .addForeColorSection(
                            if (item.msgType == 1) "" else item.content,
                            ContextCompat.getColor(context, R.color.c_ffffff)
                        )
                    if (item.identityType == 0||item.identityType == 3) {
                        binding.tvContent.text = section.spanStrBuilder
                    } else {
                        if (item.msgType == 0) {

                            binding.tvContent.text =
                                "<font color=\"#34A853\"><strong>${vm.anchorName} : </strong></font>${item.content}".toHtml()
                            //binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
                        } else {
                            //图片
                            binding.tvContent.text =
                                "<font color=\"#34A853\"><strong>${vm.anchorName} : </strong></font>".toHtml()
                        }
                    }
                    //.showIn(binding.tvContent) //显示到控件TextView中
                }
            }
        }
    }

}


fun agentWeb(
    context: Context,
    vg: ViewGroup,
    finishLoad: (Boolean) -> Unit,
): AgentWeb {

   val mAgentWeb = AgentWeb.with(context as Activity)
        .setAgentWebParent(
            vg,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        .closeIndicator()
        // .setWebView(webView)
        //.setWebView(NestedScrollAgentWebView(context))
        .setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        })
        .setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?,
            ): Boolean {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, request?.url)
                )
                return true
            }

            override fun onPageFinished(webView: WebView, url: String?) {
                super.onPageFinished(webView, url)
                Log.e("=====", "onPageFinished: ====")
                // 获取WebView内容的高度
                finishLoad.invoke(true)
            }
        })
        // .setWebView(binding.agentWeb)
        .createAgentWeb()

        .ready()
        .get()
    mAgentWeb.agentWebSettings.webSettings.apply {
        javaScriptEnabled = true
        allowFileAccess = true
        allowFileAccessFromFileURLs = true
    }




    mAgentWeb.webCreator.webView.apply {
        scrollBarSize = 0// 去掉滚动条
        overScrollMode = View.OVER_SCROLL_NEVER //  取消WebView中滚动或拖动到顶部、底部时的阴影
        setBackgroundColor(Color.argb(0, 0, 0, 0))
        val p = parent as WebParentLayout
        p.setBackgroundColor(Color.TRANSPARENT)
        setOnTouchListener { _, event ->
            if (event.pointerCount > 1) {
                // 多指触摸时，禁止缩放手势
                return@setOnTouchListener true
            }
            false
        }
    }
    return mAgentWeb
}
fun convertToList(rgbValue: String): List<Int> {
    val values = rgbValue.substringAfter("(").substringBeforeLast(")")
        .split(",")
        .map { it.trim().toInt() }

    return values
}
fun convertToHexColor(red: Int, green: Int, blue: Int): String {
    val hexRed = red.toString(16).padStart(2, '0') // 补全红色分量到两位数字并转为十六进制
    val hexGreen = green.toString(16).padStart(2, '0') // 补全绿色分量到两位数字并转为十六进制
    val hexBlue = blue.toString(16).padStart(2, '0') // 补全蓝色分量到两位数字并转为十六进制

    return "#$hexRed$hexGreen$hexBlue" // 返回完整的十六进制颜色值
}
//fun setH5Data(webView: WebView?,content:String="",tvColor:String="#ffffff",fontSize:Int=14,maxLine:Int=2){
//    val htmlText =
//        "<div style='display: -webkit-box; -webkit-line-clamp: ${maxLine}; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${content}</div>"
//    val bb = "<html><head><style>* {font-size: ${fontSize};color:${tvColor};font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(htmlText)}</body></html>"
//    webView?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)
//}


//fun findElementsWithoutColor( document: Document): List<Element> {
//    val allElements = document.select("*")
//    val elementsWithoutColor = mutableListOf<Element>()
//
//    for (element in allElements) {
//        val styleAttr = element.attr("style")
//        if (!styleAttr.contains("color")) {
//            elementsWithoutColor.add(element)
//        }
//    }
//
//    return elementsWithoutColor
//}
//
//// 添加白色样式
//fun addWhiteColorStyle(elements: List<Element>) {
//    for (element in elements) {
//        element.attr("style", "color: white; ${element.attr("style")}")
//    }
//}
//fun checkFontColorInHtml(htmlString: String): Boolean {
//    val document: Document = Jsoup.parse(htmlString)
//    val elementsWithColor = document.select("[style*=color]")
//
//    return elementsWithColor.isNotEmpty()
//}
//
//fun checkIfAnyElementHasColor(htmlString: String): Boolean {
//    val document: Document = Jsoup.parse(htmlString)
//    val allElements = document.select("*")
//
//    for (element in allElements) {
//        val styleAttr = element.attr("style")
//        if (styleAttr.contains("color")) {
//            return true
//        }
//    }
//
//    return false
//}
//
//fun replaceStrongWithTag(htmlString: String): String {
//    val document: Document = Jsoup.parse(htmlString)
//    val strongElements = document.select("strong")
//
//    for (element in strongElements) {
//        val newTag = Element("p")
//        newTag.text(element.text())
//        element.replaceWith(newTag)
//    }
//
//    return document.html()
//}

fun replaceStrongWithTagInString(input: String): String {
    if(input.isNotEmpty()){
        var  aaaa= input.replace("<strong>", " ").replace("</strong>", " ")
        var  bbb= aaaa.replace("<u>", " ").replace("</u>", " ")
        var  cccc= bbb.replace("<em>", " ").replace("</em>", " ")
        return  cccc
    }else{
        return  input
    }




}





fun setH5Data(webView: WebView?,content:String="",tvColor:String="#ffffff",fontSize:Int=14,maxLine:Int=2){
//      val document: Document
//    document = Jsoup.parse(content)
//    val elementsWithoutColor =findElementsWithoutColor( document)
//    addWhiteColorStyle(elementsWithoutColor)
//    val modifiedHtml = document.toString()
//    Log.i("SSSS","========"+checkIfAnyElementHasColor(content))
//    if(!checkIfAnyElementHasColor(content)){
//    val htmlText =
//        "<div style='display: -webkit-box; -webkit-line-clamp: ${maxLine}; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${content}</div>"
//    val bb = "<html><head><style>* {font-size: ${fontSize};color:${tvColor};font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(htmlText)}</body></html>"
//    webView?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)
//    }else{
//        val htmlText =
//            "<div style='display: -webkit-box; -webkit-line-clamp: ${maxLine}; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${content}</div>"
//        val bb = "<html><head><style>* {font-size: ${fontSize}; font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(htmlText)}</body></html>"
//        webView?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)
//    }
//    val htmlText =
//        "<div style='display: -webkit-box; -webkit-line-clamp: ${maxLine}; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${content}</div>"
//    val bb = "<html><head><style>* {font-size: ${fontSize}; font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(htmlText)}</body></html>"
//    webView?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)

    val replacedString = replaceStrongWithTagInString(content)

        val htmlText =
        "<div style='display: -webkit-box; -webkit-line-clamp: ${maxLine}; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${replacedString}</div>"
    val bb = "<html><head><style>*{font-size: ${fontSize};color:${tvColor};font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(htmlText)}</body></html>"
    Log.i("SSSSSS","==="+bb)



    webView?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)

}

/**
 * 不要最多行数
 */
fun setH5DataMaxLine(webView: WebView?,content:String="",tvColor:String="#ffffff",fontSize:Int=14){


    val replacedString = replaceStrongWithTagInString(content)

    val htmlText =
        "<div style='display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${replacedString}</div>"
    val bb = "<html><head><style>*{font-size: ${fontSize};color:${tvColor};font-family: 'PingFang SC';margin: 0;padding: 0;word-wrap: break-word;}body {padding-left: 0px;padding-right: 0px;}img {max-width: 100%;height: auto;}</style></head><body>${(htmlText)}</body></html>"
    Log.i("SSSSSS","==="+bb)
    webView?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)

}



var  showDialog: PopupKickOut?=null
var popwindow: BasePopupView?=null
fun placeLoginDialog(context: Context,finishLoad: (Boolean) -> Unit){
    if(showDialog==null){
        showDialog= PopupKickOut(context)
        popwindow= XPopup.Builder(context)
            .hasShadowBg(true)
            .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
            .isViewMode(false)
            .isClickThrough(false)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
            //                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
            .asCustom(showDialog)
    }
    showDialog!!.popupKickOutListener=object :PopupKickOut.PopupKickOutListener{
        override fun clickClose() {

        }

    }

    if(!popwindow!!.isShow){
        popwindow!!.show()
    }
}