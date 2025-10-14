package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.topup.databinding.FragmentFiatBinding
import kotlin.reflect.KClass

/**
 * 充值-法币页面
 */

class FiatFragment : BaseFragment<EmptyViewModel, FragmentFiatBinding>() {

    override val vbClass: KClass<FragmentFiatBinding> = FragmentFiatBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}