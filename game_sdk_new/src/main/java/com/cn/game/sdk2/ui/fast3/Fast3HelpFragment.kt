package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.utils.ext.ViewExt.bindRecycleView
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace
import com.drake.brv.utils.setup
import com.xcjh.base_lib.base.fragment.BaseVmVbFragment
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.view.clickNoRepeat

class Fast3HelpFragment : BaseVmVbFragment<EmptyViewModel, FragmentFast3HelpBinding>() {
    private var rootHeight:Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.rvContent
            .dividerSpace(
                requireContext().dp2px(20),
                DividerOrientation.VERTICAL
            )
            .setup {
                addType<Int> { pos ->
                    when (pos) {
                        0 -> R.layout.item_fast3_help_1
                        1 -> R.layout.item_fast3_help_2
                        2 -> R.layout.item_fast3_help_3
                        3 -> R.layout.item_fast3_help_4
                        else -> R.layout.item_fast3_help_5
                    }
                }
            }.models = listOf(1, 2, 3, 4, 5)
        mViewBind.indicator.bindRecycleView(
            mViewBind.rvContent, arrayOf(
                getString(R.string.g_home_txt_default),
                getString(R.string.g_home_tab_single),
                getString(R.string.g_home_tab_sum),
                getString(R.string.g_home_tab_double),
                getString(R.string.g_home_tab_double),
            ),
            scrollEnable = true,
            action = {
                PromptSoundPlay.btnPlayMedia()
            }
        )

        mViewBind.ivCollapse.clickNoRepeat {
            if(rootHeight == 0) rootHeight = mViewBind.root.height
            val height = if (rootHeight == mViewBind.root.height) rootHeight/2 else rootHeight
            mViewBind.root.layoutParams.height = height
            mViewBind.root.requestLayout()
        }
        mViewBind.close.clickNoRepeat {
            ViewHelper.showHelpDialog(requireContext(),false)
        }
    }


    override fun lazyLoadData() {
    }

    override fun createObserver() {
    }

}