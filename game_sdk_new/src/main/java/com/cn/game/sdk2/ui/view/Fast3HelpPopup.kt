package com.cn.game.sdk2.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.fast3.Fast3HelpFragment.Companion.TAG
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.bindRecycleView
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat
import com.xcjh.base_lib.utils.view.getStringArray

/**
 * 首页的弹出框
 */
class Fast3HelpPopup(content: Context) : BottomPopupView(content) {
    private lateinit var mViewBind: FragmentFast3HelpBinding

    override fun getImplLayoutId(): Int {
        return R.layout.fragment_fast3_help
    }

    override fun onCreate() {
        super.onCreate()
        mViewBind = FragmentFast3HelpBinding.bind(popupImplView)
        this.initView()
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
            scrollEnable = false,
            action = { PromptSoundPlay.btnPlayMedia() }
        )

        mViewBind.ivCollapse.clickNoRepeat(300) {
            PromptSoundPlay.btnPlayMedia()
            val lp = mViewBind.space.layoutParams as LinearLayout.LayoutParams
            val toExpand = lp.weight == 1f
            val topPadding = if (toExpand) 40.dp2px else 0
            val bottomPadding = 48.dp2px
            mViewBind.ivCollapse.setImageResource(if (!toExpand) R.drawable.ic_expand else R.drawable.ic_collapse)
            val start = if (!toExpand) 0f else 1f
            val end = if (toExpand) 0f else 1f
            Log.d(TAG,"addUpdateListener----->$start-->$end,toExpand:$toExpand")
            ValueAnimator.ofFloat(start, end).apply {
                duration = 200
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    lp.weight = value
                    mViewBind.space.layoutParams = lp

                    Log.d(TAG,"addUpdateListener----->${lp.weight}")
                }
                addListener(
                    onStart = {
                        lp.weight = start
                        mViewBind.space.layoutParams = lp
                    },
                    onEnd = {
                        //动画结束
                        lp.weight = end
                        mViewBind.root.setPadding(0, topPadding, 0, bottomPadding)
                        mViewBind.space.layoutParams = lp
                    })
                start()
            }

        }
        mViewBind.close.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia()
            ViewHelper.showHelpDialog(context, false)
        }
    }


}