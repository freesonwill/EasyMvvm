package com.walisport.module.home.test

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentTestSecondBinding
import kotlin.reflect.KClass


class SecondFragment : BaseFragment<EmptyViewModel,FragmentTestSecondBinding>() {
    override val vbClass: KClass<FragmentTestSecondBinding> = FragmentTestSecondBinding::class
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