package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentLoginBinding
import com.walisport.module.login.databinding.FragmentRegisterBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/17 下午5:46
 * @description:
 */
class RegisterFragment: BaseFragment<EmptyViewModel, FragmentRegisterBinding>() {
    override val vbClass: KClass<FragmentRegisterBinding> = FragmentRegisterBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    private val args: LoginFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        //val userId = arguments?.getString("userId")
        with (mBinding) {
            titleBar.loadGeneralTitleBar("", {
                findNavController().navigateUp()
            })
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {
    }
}