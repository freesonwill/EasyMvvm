package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentBetDetailBinding
import com.walisport.module.topup.ui.viewmodel.BetDetailViewModel
import kotlin.reflect.KClass

/**
 * 投注额详情页面
 */

class BetDetailFragment : BaseFragment<BetDetailViewModel, FragmentBetDetailBinding>() {

    override val vbClass: KClass<FragmentBetDetailBinding> = FragmentBetDetailBinding::class
    override val vmClass: KClass<BetDetailViewModel> = BetDetailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.bet_detail, {
            findNavController().navigateUp()
        })
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}