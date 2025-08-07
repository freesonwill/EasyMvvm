package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentIdentityVerifyBinding
import com.walisport.module.login.ui.adapter.IdentityVerifyPagerAdapter
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/16 下午4:03
 * @description:
 */
class IdentifyVerifyFragment: BaseFragment<EmptyViewModel, FragmentIdentityVerifyBinding>() {
    override val vbClass: KClass<FragmentIdentityVerifyBinding> = FragmentIdentityVerifyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        //val userId = arguments?.getString("userId")
        with(mBinding) {
            titleBar.loadGeneralTitleBar(getString(R.string.identity_verify), {
                findNavController().navigateUp()
            })
            viewPager.adapter = IdentityVerifyPagerAdapter(
                childFragmentManager,
                lifecycle
            )
            TabLayoutMediator(tabLayout, viewPager){
                tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.mobile_verify)
                    else -> getString(R.string.email_verify)
                }

            }.attach()
        }

    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}