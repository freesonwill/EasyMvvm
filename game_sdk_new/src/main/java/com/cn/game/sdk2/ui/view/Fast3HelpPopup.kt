package com.cn.game.sdk2.ui.view

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.fast3.Fast3HelpFragment.Companion.TAG
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.ext.ViewExt.bindRecycleView
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.utils.tool.dp2px
import com.cn.game.sdk2.utils.tool.screenHeight
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
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import com.xcjh.base_lib2.utils.view.getStringArray


/**
 * 首页的弹出框
 */
class Fast3HelpPopup(context: Context, private val offsetY: Int, private val height: Int) :
    CustomBottomPopupView(context) {
    private lateinit var mViewBind: FragmentFast3HelpBinding

    //全屏的高度
    //private var fullHeight: Int = context.run { screenHeight + statusBarHeight + navigationBarHeight }
    private var fullHeight: Int = mActivity.run { screenHeight +if(mActivity.hasNotchScreen) notchHeight else 0  }

    //当前的高度
    private val curHeight: Int get() = mViewBind.content.height

    override fun getImplLayoutId(): Int {
        return R.layout.fragment_fast3_help
    }
    private val mActivity:Activity get() = if(context is Fragment) (context as Fragment).requireActivity() else context as Activity
    private val originalStatusBarColor = mActivity.window.statusBarColor

    override fun onCreate() {
        super.onCreate()
        /*LogUtils.dTag(TAG,"screenHeight:${mActivity.screenHeight}," +
                "statusBarHeight:${mActivity.statusBarHeight}," +
                " notchHeight:${mActivity.notchHeight}" +
                ",navigationBarHeight:${mActivity.navigationBarHeight}" +
                ",actionBarHeight:${mActivity.actionBarHeight}" +
                ",hasStatusBar:${mActivity.hasNavigationBar}"+
                ",hasNotchScreen:${mActivity.hasNotchScreen}"+
                "")*/
        mViewBind = FragmentFast3HelpBinding.bind(popupImplView)
        mViewBind.content.setPadding(0, 0, 0, mNavigationHeight)
        // 设置过度滚动效果
        this.initView()
        mViewBind.root.layoutParams.let { lp ->
            lp.height = height
            mViewBind.content.layoutParams = lp
        }
    }
    private val mNavigationHeight get() = if (context.hasNavigationBar) context.navigationBarHeight else 0
    private fun setStatusBarColor(color: Int) {
        mActivity.window.statusBarColor = color
    }

    private fun initView() {
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
                            R.layout.item_fast3_help_3
                        }
                    }
                }
            }.models = listOf(1, 2, 3, 4, 5,6,7,8,9,10,11)

        mViewBind.indicator.bindRecycleView(
            mViewBind.rvContent,
            getStringArray(R.array.help_tabs),
            scrollEnable = true,
            action = { PromptSoundPlay.btnPlayMedia() }
        )

        mViewBind.lltCollapse.clickNoRepeat(false, 300) {
            PromptSoundPlay.btnPlayMedia()
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
                        setStatusBarColor(if(toExpand) ContextCompat.getColor(context,R.color.c_141624) else originalStatusBarColor)
                        mViewBind.ivCollapse.setImageResource(if (!toExpand) R.drawable.ic_expand else R.drawable.ic_collapse)
                    })
                start()
            }
            LogUtils.d(TAG, "addUpdateListener----->$start-->$end,toExpand:$toExpand")
        }
        mViewBind.close.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia()
            ViewHelper.showHelpDialog(context, false)
        }
    }
}