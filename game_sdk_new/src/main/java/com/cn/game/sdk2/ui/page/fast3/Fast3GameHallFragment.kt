package com.cn.game.sdk2.ui.page.fast3

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.utils.ext.CommonExt.getString
import com.cn.game.sdk2.utils.ext.bindViewPagerNewGame
import com.cn.game.sdk2.utils.ext.initGameViewPager
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import org.koin.androidx.viewmodel.ext.android.viewModel

class Fast3GameHallFragment: BaseFragment<EmptyViewModel,FragmentGamehallBinding>() {
    private var mFragList = ArrayList<Fragment>()
    override val mBinding: FragmentGamehallBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            mFragList.apply {
                add(Fast3GameHallItemFragment())
                add(Fast3GameHallItemFragment())
                add(Fast3GameHallItemFragment())
                add(Fast3GameHallItemFragment())
                add(Fast3GameHallItemFragment())
            }

            val gameTypes = arrayListOf(
                R.string.g_home_txt_default.getString(),
                R.string.g_home_tab_single.getString(),
                R.string.g_home_tab_sum.getString(),
                R.string.g_home_tab_double.getString(),
                R.string.g_home_tab_leopard.getString(),
            )

            viewPagerNew.apply {
                initGameViewPager(childFragmentManager, mFragList, gameTypes)
                offscreenPageLimit = mFragList.size
            }
            magicIndicator.bindViewPagerNewGame(
                viewPagerNew,
                gameTypes,
                scrollEnable = true,
                action = { PromptSoundPlay.btnPlayMedia() }
            )

            close.clickNoRepeat(true) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    override fun lazyLoadData() {

    }

    override fun createObserver() {

    }
}