package com.cn.game.sdk2.ui.popup.fast3

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.popup.CustomBottomPopupView
import com.cn.game.sdk2.utils.ext.ViewExt.bindRecycleView
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ktx.actionBarHeight
import com.gyf.immersionbar.ktx.hasNavigationBar
import com.gyf.immersionbar.ktx.hasNotchScreen
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.gyf.immersionbar.ktx.notchHeight
import com.gyf.immersionbar.ktx.statusBarHeight
import com.xcjh.base_lib2.utils.LogUtils
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.DensityExt.px2dp
import com.gyf.immersionbar.ktx.isGesture
import com.gyf.immersionbar.ktx.isNavigationAtBottom
import com.xcjh.base_lib2.utils.screenHeight
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import com.xcjh.base_lib2.utils.view.getStringArray


/**
 * 首页的弹出框
 */
class Fast3HelpPopup(context: Context, private val offsetY: Int, private val height: Int) :
    CustomBottomPopupView(context) {
    companion object {
        const val TAG = "Fast3HelpPopup"
    }

    private lateinit var mViewBind: FragmentFast3HelpBinding

    //全屏的高度
    private val fullHeight = mActivity.run {
        when {
            //刘海屏: 刘海高度取代了状态栏高度
            hasNotchScreen -> activityContentView.height - notchHeight
            //非刘海屏
            else -> if (!isGesture) activityContentView.height-navigationBarHeight/2 else (activityContentView.height - navigationBarHeight)
        }
    }

    //当前的高度
    private val curHeight: Int get() = mViewBind.content.height

    override fun getImplLayoutId(): Int {
        return R.layout.fragment_fast3_help
    }

    private val mActivity: Activity get() = if (context is Fragment) (context as Fragment).requireActivity() else context as Activity
    private val originalStatusBarColor = mActivity.window.statusBarColor

    override fun onCreate() {
        super.onCreate()
        LogUtils.dTag(
            TAG, "screenHeight:${mActivity.screenHeight}," +
                    "statusBarHeight:${mActivity.statusBarHeight}," +
                    " notchHeight:${mActivity.notchHeight}" +
                    ",navigationBarHeight:${mActivity.navigationBarHeight}" +
                    ",isNavigationAtBottom:${mActivity.isNavigationAtBottom}" +
                    ",isGesture:${mActivity.isGesture}" +
                    ",actionBarHeight:${mActivity.actionBarHeight}" +
                    ",hasNavigationBar:${mActivity.hasNavigationBar}" +
                    ",hasNotchScreen:${mActivity.hasNotchScreen}" +
                    ",activityContentViewH:" + activityContentView.height +
                    ",88:${88.px2dp}" +
                    ""
        )
        mViewBind = FragmentFast3HelpBinding.bind(popupImplView)
        // 设置过度滚动效果
        this.initView()
        mViewBind.root.layoutParams.let { lp ->
            lp.height = height
            mViewBind.content.layoutParams = lp
        }
    }

    private val mNavigationHeight
        get() = if (context.hasNavigationBar) context.navigationBarHeight else {
            if (context.isGesture) 30.dp2px else 0
        }

    private fun setStatusBarColor(color: Int) {
        mActivity.window.statusBarColor = color
    }

    private fun initView() {
        mViewBind.rvContent.layoutManager = LinearLayoutManager(context)
        mViewBind.rvContent
            .dividerSpace(
                context.dp2px(20),
                DividerOrientation.VERTICAL
            )
            .setup {
                addType<Int> { pos ->
                    when (pos) {
                        0 -> R.layout.item_fast3_help_1
                        1 -> R.layout.item_fast3_help_2
                        9 -> R.layout.item_fast3_help_4
                        10 -> R.layout.item_fast3_help_5
                        else -> {
                            context.resources.getIdentifier(
                                "layout_fast3_help3_${pos - 1}",
                                "layout",
                                context.packageName
                            )
                        }
                    }
                }
            }.models = List(11) { it }

        mViewBind.indicator.bindRecycleView(
            mViewBind.rvContent,
            getStringArray(R.array.help_tabs),
            scrollEnable = true,
            action = { PromptSoundPlay.btnPlayMedia() }
        )

        mViewBind.lltCollapse.clickNoRepeat(true, 300) {
            val toExpand = mViewBind.content.height != fullHeight
            val topPadding = if (toExpand) 0 else 0
            val topPaddingFrom = if (!toExpand) 0 else 0
            val bottomPadding = mNavigationHeight
            val start = if (toExpand) height else fullHeight
            val end = if (!toExpand) height else fullHeight

            ValueAnimator.ofInt(start, end).apply {
                duration = 100
                addUpdateListener {
                    val value = it.animatedValue as Int
                    val p = (value - start) * 1f / (end - start)
                    val tPadding = 0
                    mViewBind.content.setPadding(0, tPadding, 0, bottomPadding)
                    mViewBind.content.layoutParams.let { lp ->
                        lp.height = value
                        mViewBind.content.layoutParams = lp
                    }
                }
                addListener(
                    onStart = {
                        mViewBind.content.setPadding(0, topPadding, 0, bottomPadding)
                    },
                    onEnd = {
                        //动画结束
                        setStatusBarColor(
                            if (toExpand) ContextCompat.getColor(
                                context,
                                R.color.c_141624
                            ) else originalStatusBarColor
                        )
                        mViewBind.ivCollapse.setImageResource(if (!toExpand) R.drawable.game_sdk_ic_expand else R.drawable.game_sdk_ic_collapse)
                    })
                start()
            }
            LogUtils.dTag(TAG, "addUpdateListener----->$start-->$end,toExpand:$toExpand")
        }
        mViewBind.close.clickNoRepeat(true) {
            ViewHelper.instance.showHelpDialog(context, false, height)
        }
    }
}