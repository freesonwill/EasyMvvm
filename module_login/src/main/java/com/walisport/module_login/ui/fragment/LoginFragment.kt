package com.walisport.module_login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.lib_base.utils.LogUtilsExt.logd
import com.walisport.module_login.R
import com.walisport.module_login.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author: zhangsan
 * @date: 2025/3/26 12:02
 * @description:
 */
class LoginFragment : BaseFragment<EmptyViewModel, FragmentLoginBinding>() {
    override val mBinding: FragmentLoginBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()
    private val args: LoginFragmentArgs by navArgs()
    private val TAG = this.javaClass.simpleName

    override fun initView(savedInstanceState: Bundle?) {
        //val userId = arguments?.getString("userId")
        val userId = args.userId
        "LoginFragment--->$userId,arguments:$arguments,args:$args".logd(TAG)
        mBinding.username.setText(userId)
    }

    override fun initListener() {
        mBinding.login.setOnClickListener {
            findNavController().navigate(R.id.loginSecondFragment)
        }
    }

    override fun createObserver() {
    }
}