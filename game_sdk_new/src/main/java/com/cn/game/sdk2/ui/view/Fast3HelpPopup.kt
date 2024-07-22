package com.cn.game.sdk2.ui.view

import android.animation.ValueAnimator
import android.content.Context
import androidx.core.animation.addListener
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
import com.gyf.immersionbar.ktx.hasNavigationBar
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.gyf.immersionbar.ktx.statusBarHeight
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import com.xcjh.base_lib2.utils.view.getStringArray


/**
 * 首页的弹出框
 */
class Fast3HelpPopup(context: Context, private val offsetY: Int, private val height: Int) : CustomBottomPopupView(context) {
    private lateinit var mViewBind: FragmentFast3HelpBinding

    //全屏的高度
    private var fullHeight: Int = context.run { screenHeight + statusBarHeight + navigationBarHeight }

    //当前的高度
    private val curHeight: Int get() = mViewBind.content.height

    override fun getImplLayoutId(): Int {
        return R.layout.fragment_fast3_help
    }

    override fun onCreate() {
        super.onCreate()
        mViewBind = FragmentFast3HelpBinding.bind(popupImplView)
        /*mViewBind.rvContent.setEdgeEffectFactory(object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return BounceEdgeEffect(view.context, view)
            }
        })*/
        // 设置过度滚动效果
        this.initView()
        mViewBind.root.layoutParams.let { lp ->
            lp.height = height
            mViewBind.content.layoutParams = lp
        }
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
                        2 -> R.layout.item_fast3_help_3
                        3 -> R.layout.item_fast3_help_4
                        4 -> R.layout.item_fast3_help_5
                        else -> {
                            throw IllegalStateException("error pos:$pos")
                        }
                    }
                }
            }.models = listOf(1, 2, 3, 4, 5)

        mViewBind.indicator.bindRecycleView(
            mViewBind.rvContent,
            getStringArray(R.array.help_tabs),
            scrollEnable = true,
            action = { PromptSoundPlay.btnPlayMedia() }
        )

        mViewBind.lltCollapse.clickNoRepeat(false,300) {
            PromptSoundPlay.btnPlayMedia()
            val toExpand = mViewBind.content.height != fullHeight
            val topPadding = if (toExpand) context.statusBarHeight else 0
            val topPaddingFrom = if (!toExpand) context.statusBarHeight else 0
            val bottomPadding = if (context.hasNavigationBar) context.navigationBarHeight else 0
            val start = if (toExpand) height else fullHeight
            val end = if (!toExpand) height else fullHeight
            ValueAnimator.ofInt(start, end).apply {
                duration = 100
                addUpdateListener {
                    val value = it.animatedValue as Int
                    val p = (value - start) * 1f / (end - start)
                    val tPadding = (topPaddingFrom + (topPadding - topPaddingFrom) * p).toInt()
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