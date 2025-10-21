package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentBetDetailBinding
import kotlin.reflect.KClass


/**
 * 充值和提现教程页面
 */

class LessonFragment : BaseFragment<EmptyViewModel, FragmentBetDetailBinding>() {

    override val vbClass: KClass<FragmentBetDetailBinding> = FragmentBetDetailBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val isTopUp = arguments?.getBoolean("isTopUp") ?: false
        if (isTopUp) {
            mBinding.titleBar.loadGeneralTitleBar(R.string.recharge_lesson, {
                findNavController().navigateUp()
            })
        } else {
            mBinding.titleBar.loadGeneralTitleBar(R.string.withdraw_lesson, {
                findNavController().navigateUp()
            })
        }
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}