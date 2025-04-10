package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
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