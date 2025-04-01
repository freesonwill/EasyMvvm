package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val mBinding: FragmentLiveVideoBinding by viewBind()
    override val mViewModel: LiveVideoViewModel by viewModel()


    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
    }

    override fun createObserver() {

    }
}