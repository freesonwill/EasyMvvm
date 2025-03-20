package com.walisport.app.ui.fragment

import android.os.Bundle
import com.walisport.app.databinding.FragmentHomeBinding
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<EmptyViewModel,FragmentHomeBinding>() {
    override val mBinding: FragmentHomeBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.goToSettingMain.setOnClickListener {
        }
    }

    override fun createObserver() {
    }

    override fun lazyLoadData() {
    }

}