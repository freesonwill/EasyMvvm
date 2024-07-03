package com.cn.game.sdk2.ui.fast3

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.xcjh.base_lib.base.fragment.BaseVmDbFragment

class Fast3GameHallFragment:BaseVmDbFragment<EmptyViewModel,FragmentGamehallBinding>() {
    private var mFragList = ArrayList<Fragment>()

    override fun initView(savedInstanceState: Bundle?) {
        mFragList.add(Fast3GameHallItemFragment("a"))
        mFragList.add(Fast3GameHallItemFragment("a"))
        mFragList.add(Fast3GameHallItemFragment("a"))
        mFragList.add(Fast3GameHallItemFragment("a"))
        mFragList.add(Fast3GameHallItemFragment("a"))

        mDatabind.viewPagerNew.initGameViewPager(
            childFragmentManager, mFragList, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
                requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_sum),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard)
            )
        )
        mDatabind.magicIndicator.bindViewPagerNewGame(
            mDatabind.viewPagerNew, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
                requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_sum),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard)
            ),
            scrollEnable = true,
            action = {
                PromptSoundPlay.btnPlayMedia()
            }
        )
    }


    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }
}