package com.walisport.module.home.ui.fragment

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.data.PlayType
import com.walisport.module.home.databinding.FragmentNewHomeBinding
import com.walisport.module.home.viewmodel.HomeViewModel
import kotlin.reflect.KClass

class NewHomeFragment : BaseFragment<HomeViewModel, FragmentNewHomeBinding>() {
    override val vbClass: KClass<FragmentNewHomeBinding> = FragmentNewHomeBinding::class
    override val vmClass: KClass<HomeViewModel> = HomeViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.initHomeData()
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}