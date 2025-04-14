package com.walisport.module.home.test

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.module.home.databinding.FragmentTestSecondBinding
import kotlin.reflect.KClass


class SecondFragment : BaseFragment<EmptyViewModel, FragmentTestSecondBinding>() {
    override val vbClass: KClass<FragmentTestSecondBinding> = FragmentTestSecondBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.root.setOnClickListener {
            navigate(SecondFragmentDirections.actionSecondFragmentToThirdFragment())
        }
    }

    override fun createObserver() {
    }

}