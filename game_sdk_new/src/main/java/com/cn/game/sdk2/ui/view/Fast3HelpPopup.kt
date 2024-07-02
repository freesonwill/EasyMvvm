package com.cn.game.sdk2.ui.view

import android.content.Context
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.ext.BindingAdapterUtil.bindRecycleView
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.BubbleAttachPopupView
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 首页的弹出框
 */
class Fast3HelpPopup(content: Context) : BottomPopupView(content) {
    private var rootHeight: Int = 0
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
                        else ->{ throw  IllegalStateException("error pos:$pos")}
                    }
                }
            }.models = listOf(1, 2, 3, 4, 5)
        mViewBind.indicator.bindRecycleView(
            mViewBind.rvContent,
            //Todo 放到array.xml中
            arrayListOf(
                "基础规则",
                "游戏规则",
                "注区",
                "赔率",
                "路单",
            ),
            scrollEnable = false,
            action = {
                PromptSoundPlay.btnPlayMedia()
            }
        )

        mViewBind.ivCollapse.clickNoRepeat {
            if (rootHeight == 0) rootHeight = mViewBind.root.height
            val isExpand = rootHeight != mViewBind.root.height
            val height = if (!isExpand) rootHeight / 2 else rootHeight
            mViewBind.root.layoutParams.height = height
            //mViewBind.root.requestLayout()
            mViewBind.ivCollapse.setImageResource(if(isExpand) R.drawable.ic_expand else R.drawable.ic_collapse)
        }
        mViewBind.close.clickNoRepeat {
            ViewHelper.showHelpDialog(context, false)
        }
    }
}