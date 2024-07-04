package com.cn.game.sdk2.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import androidx.core.animation.addListener
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.fast3.Fast3HelpFragment.Companion.TAG
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.bindRecycleView
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.cn.game.sdk2.websocket.gameAboutModel
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
class Fast3HelpPopup(context: Context,private val offsetY:Int) : BottomPopupView(context) {
    private lateinit var mViewBind: FragmentFast3HelpBinding
    override fun getImplLayoutId(): Int {
        return R.layout.fragment_fast3_help
    }

    override fun onCreate() {
        super.onCreate()
        mViewBind = FragmentFast3HelpBinding.bind(popupImplView)
        mViewBind.content.translationY = offsetY.toFloat()
        this.initView()
        gameAboutModel.fast3MainFloatVisible.value = false

    }

    override fun onDestroy() {
        super.onDestroy()
        gameAboutModel.fast3MainFloatVisible.value = true
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

        mViewBind.ivCollapse.clickNoRepeat(300) {
            PromptSoundPlay.btnPlayMedia()
            val toExpand = mViewBind.content.translationY != 0f
            val topPadding = if (toExpand) 40.dp2px else 0
            val bottomPadding = 48.dp2px
            val start = if (toExpand) offsetY else 0
            val end = if (!toExpand) offsetY else 0
            ObjectAnimator.ofFloat(mViewBind.content, "translationY", start.toFloat(), end.toFloat()).apply {
                duration = 200
                addListener(
                    onStart = {
                        mViewBind.content.translationY = start.toFloat()
                        mViewBind.content.setPadding(0, topPadding, 0, bottomPadding)
                    },
                    onEnd = {
                        //动画结束
                        mViewBind.ivCollapse.setImageResource(if (!toExpand) R.drawable.ic_expand else R.drawable.ic_collapse)
                    })
                start()
            }
            Log.d(TAG,"addUpdateListener----->$start-->$end,toExpand:$toExpand")
        }
        mViewBind.close.clickNoRepeat {
            PromptSoundPlay.btnPlayMedia()
            ViewHelper.showHelpDialog(context, false)
        }
    }


}