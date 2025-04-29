package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentLoginBinding
import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/3/26 12:02
 * @description:
 */
class LoginFragment : BaseFragment<EmptyViewModel, FragmentLoginBinding>() {
    override val vbClass: KClass<FragmentLoginBinding> = FragmentLoginBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private val args: LoginFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        //val userId = arguments?.getString("userId")
        val userId = args.userId
        "LoginFragment--->$userId,arguments:$arguments,args:$args".logd(TAG)
        mBinding.username.setText(userId)
    }

    override fun initListener() {
        mBinding.login.setOnClickListener {
            navigate(R.id.loginSecondFragment)
        }
    }

    override fun createObserver() {
    }
}