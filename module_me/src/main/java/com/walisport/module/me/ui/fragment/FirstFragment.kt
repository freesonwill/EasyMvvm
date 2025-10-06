package com.walisport.module.me.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.me.R
import com.walisport.module.me.databinding.FragmentFirstBinding
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlin.reflect.KClass

/**
 * 我的界面
 */

class FirstFragment : BaseFragment<MeViewModel, FragmentFirstBinding>() {

    override val vbClass: KClass<FragmentFirstBinding> = FragmentFirstBinding::class
    override val vmClass: KClass<MeViewModel> = MeViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

        mBinding.button1.clickNoRepeat { navigate(FirstFragmentDirections.actionFirstFragmentToSecondFragment()) }
        mBinding.button2.clickNoRepeat {
            requireActivity().navigate(Uri.parse("walisport://module_live/liveFragment?matchId=123456&sportId=1"))
        }
    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }


}