package com.cn.game.sdk2.ui.fast3

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentFast3HelpBinding
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
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
    companion object {
        const val TAG = "Fast3HelpFragment"
    }
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

        mViewBind.ivCollapse.clickNoRepeat(500) {
            /*val lp = mViewBind.space.layoutParams as LinearLayout.LayoutParams
            val isExpand = lp.weight != 0f
            val start = if(isExpand) 0f else 1f
            val end = if(!isExpand) 0f else 1f
            *//*lp.weight = end
            mViewBind.space.layoutParams = lp*//*
            ValueAnimator.ofFloat(start, end).apply {
                duration = 10000
                addUpdateListener {
                    lp.weight = it.animatedValue as Float
                    Log.d(TAG,"addUpdateListener----->${lp.weight}")
                    mViewBind.space.layoutParams = lp
                }
                addListener(
                    onStart = {
                        lp.weight = start
                        mViewBind.space.layoutParams = lp
                    },
                    onEnd = {
                        lp.weight = end
                        mViewBind.space.layoutParams = lp
                        val icon =  if(!isExpand) R.drawable.ic_expand else R.drawable.ic_collapse
                        mViewBind.ivCollapse.setImageResource(icon)
                    }
                )
                start()
            }*/

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