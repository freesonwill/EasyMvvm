package com.walisport.module.home.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.home.databinding.FragmentSecondBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SecondFragment : BaseFragment<EmptyViewModel,FragmentSecondBinding>() {
    override val mBinding: FragmentSecondBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.root.setOnClickListener {
            findNavController().navigate(SecondFragmentDirections.actionSecondFragmentToThirdFragment())
        }
    }

    override fun createObserver() {
    }

}