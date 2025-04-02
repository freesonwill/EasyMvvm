package com.walisport.module.live.ui

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveMainBinding
import com.walisport.module.live.viewmodel.LiveMainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveMainFragment : BaseFragment<LiveMainViewModel, FragmentLiveMainBinding>() {
    override val mBinding: FragmentLiveMainBinding by viewBind()

    override val mViewModel: LiveMainViewModel by viewModel()


    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.ivBack.setOnClickListener { findNavController().navigateUp() }

        mViewModel.viewModelScope
    }

    override fun createObserver() {
    }
}