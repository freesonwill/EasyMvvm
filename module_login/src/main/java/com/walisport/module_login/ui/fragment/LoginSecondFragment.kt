package com.walisport.module_login.ui.fragment

import android.os.Bundle
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module_login.databinding.FragmentLoginSecondBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginSecondFragment : BaseFragment<EmptyViewModel,FragmentLoginSecondBinding>(){
    override val mBinding: FragmentLoginSecondBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}