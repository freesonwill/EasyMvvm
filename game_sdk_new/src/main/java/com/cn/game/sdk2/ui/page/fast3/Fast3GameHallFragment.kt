package com.cn.game.sdk2.ui.page.fast3

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.ui.page.fast3.Fast3GameHallItemFragment
import com.cn.game.sdk2.ui.helper.ViewHelper.bindViewPagerNewGame
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.xcjh.base_lib2.base.fragment.BaseVmDbFragment
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import org.koin.androidx.viewmodel.ext.android.viewModel

class Fast3GameHallFragment:BaseVmDbFragment<EmptyViewModel,FragmentGamehallBinding>() {
    private var mFragList = ArrayList<Fragment>()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mFragList.add(Fast3GameHallItemFragment())
        mFragList.add(Fast3GameHallItemFragment())
        mFragList.add(Fast3GameHallItemFragment())
        mFragList.add(Fast3GameHallItemFragment())
        mFragList.add(Fast3GameHallItemFragment())

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
            action = { PromptSoundPlay.btnPlayMedia() }
        )
        mDatabind.viewPagerNew.offscreenPageLimit = mFragList.size

        mDatabind.close.clickNoRepeat(true) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }
}