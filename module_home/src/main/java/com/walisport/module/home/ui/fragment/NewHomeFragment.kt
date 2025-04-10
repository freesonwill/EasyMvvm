package com.walisport.module.home.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentNewHomeBinding
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<EmptyViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}