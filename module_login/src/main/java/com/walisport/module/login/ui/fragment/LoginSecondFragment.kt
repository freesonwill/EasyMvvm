package com.walisport.module.login.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.module.login.databinding.FragmentLoginSecondBinding
import kotlin.reflect.KClass

class LoginSecondFragment : BaseFragment<EmptyViewModel, FragmentLoginSecondBinding>() {
    override val vbClass: KClass<FragmentLoginSecondBinding> = FragmentLoginSecondBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}