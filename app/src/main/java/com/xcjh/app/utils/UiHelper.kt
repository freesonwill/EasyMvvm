package com.xcjh.app.utils

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
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
import com.xcjh.app.R
import com.xcjh.app.adapter.ViewPager2Adapter
import com.xcjh.app.bean.FirstMsgBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.NoticeBean
import com.xcjh.app.bean.TabBean
import com.xcjh.app.databinding.HeadViewBinding
import com.xcjh.app.databinding.ItemDetailChatBinding
import com.xcjh.app.databinding.ItemDetailChatFirstBinding
import com.xcjh.app.ui.details.fragment.DetailAnchorFragment
import com.xcjh.app.ui.details.fragment.DetailChat2Fragment
import com.xcjh.app.ui.details.fragment.DetailChatFragment
import com.xcjh.app.ui.details.fragment.DetailIndexFragment
import com.xcjh.app.ui.details.fragment.DetailLineUpFragment
import com.xcjh.app.ui.details.fragment.DetailLiveFragment
import com.xcjh.app.ui.details.fragment.DetailResultFragment
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.SpanUtil
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
            0 -> "比赛异常"//此处可以隐藏处理，看UI设计
            1 -> "未开赛"
            2 -> "比赛中"//"上半场"
            3 -> "中场"
            4 -> "比赛中"//"下半场"
            5 -> "加时赛"
            6 -> "加时赛"
            7 -> "点球决战"
            8 -> "已完场"
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
            10 -> "已完场"
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
                5 -> "加时"
                7 -> "点球"
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
                2 -> "第一节"
                3 -> "第一节完"
                4 -> "第二节"
                5 -> "第二节完"
                6 -> "第三节"
                7 -> "第三节完"
                8 -> "第四节"
                9 -> "加时"
                else -> "比赛异常"
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
            1 -> mFragList.add(DetailChatFragment(liveId, anchorId))
            //1 -> mFragList.add(DetailChat2Fragment(liveId, anchorId))
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
fun setChatRoomRcv(
    rcvChat: RecyclerView,
    mLayoutManager: HoverLinearLayoutManager,
    isReverse: Boolean = false,
    action: (AgentWeb) -> Unit = {},
    offset: (String?) -> Unit = {},
) {
    rcvChat.apply {
        layoutManager = mLayoutManager
    }.setup {
        lateinit var mAgentWeb: AgentWeb
        addType<NoticeBean> {
            R.layout.item_detail_chat_notice // 公告
        }
        addType<FirstMsgBean> {
            R.layout.item_detail_chat_first // 公告
        }
        addType<MsgBean> {
            R.layout.item_detail_chat // 我发的消息
        }
        onCreate {
            when (itemViewType) {
                R.layout.item_detail_chat_first -> {
                    val binding = getBinding<ItemDetailChatFirstBinding>()
                    mAgentWeb = AgentWeb.with(rcvChat.context as Activity)
                        .setAgentWebParent(
                            binding.agentWeb,
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
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean {
                                rcvChat.context.startActivity(Intent(Intent.ACTION_VIEW, request?.url))
                                return true
                            }
                        })
                        // .setWebView(binding.agentWeb)
                        .createAgentWeb()
                        .ready()
                        .get()
                    // 去掉滚动条
                    mAgentWeb.webCreator.webView.scrollBarSize = 0
                    //  取消WebView中滚动或拖动到顶部、底部时的阴影
                    mAgentWeb.webCreator.webView.overScrollMode = View.OVER_SCROLL_NEVER
                    mAgentWeb.agentWebSettings.webSettings.javaScriptEnabled = true
                    mAgentWeb.agentWebSettings.webSettings.allowFileAccess = true
                    mAgentWeb.agentWebSettings.webSettings.allowFileAccessFromFileURLs = true //c_181819
                    mAgentWeb.webCreator.webView.setBackgroundColor(Color.argb(0, 0, 0, 0))
                    val p = mAgentWeb.webCreator.webView.parent as WebParentLayout
                    p.setBackgroundColor(Color.TRANSPARENT)
                    action.invoke(mAgentWeb)
                }
            }
        }
        onBind {
            when (val item = _data) {
                is NoticeBean -> {

                }

                is FirstMsgBean -> {
                    //主播加入超链接
                    /* "<font color=\"#34A853\" font-weight=\"500\">${item.nick} : </font>${item.content}".toHtml {
                         Handler(Looper.getMainLooper()).post {
                             binding.tvContent.text = it
                         }
                     }*/
                    /* val bb = item.content.replaceFirst("<p>", "<span>")
                         .replace("</p>", "</span>")*/
                    val bb =
                        "<html><head><style>body { font-size:14px; color: #ffffff; margin: 0; }</style></head><body>${item.content}</body></html>"
                    // binding.webView.loadData(aa+javascript,"text/html", "UTF-8") //aa.toHtml()
                    mAgentWeb.urlLoader.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)
                }

                is MsgBean -> {
                    val binding = getBinding<ItemDetailChatBinding>()
                    setTextBold(binding.tvLevel, item.identityType != 0)
                    if (item.identityType == 0) {
                        binding.ivImage.visibleOrGone(false)
                        binding.ivLevel.visibleOrGone(true)
                        binding.lltLevel.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.c_1AFFFFFF)
                        )
                        binding.ivLevel.setImageResource(setLeverDrawable(item.level))
                        binding.tvLevel.text = getLeverNum(item.level)
                    } else {
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
                            //item.id?.loge("====+++++++++")
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
                    if (item.identityType == 0) {
                        binding.tvContent.text = section.spanStrBuilder
                    } else {
                        if (item.msgType == 0) {
                            binding.tvContent.text =
                                "<font color=\"#34A853\"><strong>${item.nick} : </strong></font>${item.content}".toHtml()
                            //binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
                        } else {
                            //图片
                            binding.tvContent.text =
                                "<font color=\"#34A853\"><strong>${item.nick} : </strong></font>".toHtml()
                        }
                    }
                    //.showIn(binding.tvContent) //显示到控件TextView中
                }
            }
        }
    }
    fun setWebView() {

    }
}

