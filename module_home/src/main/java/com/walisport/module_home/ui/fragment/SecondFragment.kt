package com.walisport.module.home.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentSecondBinding
import kotlin.reflect.KClass


class SecondFragment : BaseFragment<EmptyViewModel,FragmentSecondBinding>() {
    override val vbClass: KClass<FragmentSecondBinding> = FragmentSecondBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

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