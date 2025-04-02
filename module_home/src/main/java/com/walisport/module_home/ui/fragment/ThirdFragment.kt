package com.walisport.module.home.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.home.databinding.FragmentThirdBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class ThirdFragment : BaseFragment<EmptyViewModel,FragmentThirdBinding>() {
    override val mBinding: FragmentThirdBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.root.setOnClickListener {
            findNavController().navigate(ThirdFragmentDirections.actionThirdFragmentToHomeFragment())
        }
    }

    override fun createObserver() {
    }

}