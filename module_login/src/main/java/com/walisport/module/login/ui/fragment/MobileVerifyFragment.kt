package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentEmailVerifyBinding
import com.walisport.module.login.databinding.FragmentMobileVerifyBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/16 下午4:18
 * @description:
 */
class MobileVerifyFragment: BaseFragment<EmptyViewModel, FragmentMobileVerifyBinding>() {
    override val vbClass: KClass<FragmentMobileVerifyBinding> = FragmentMobileVerifyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        with(mBinding) {
            btnNext.clickNoRepeat {
                navigate(R.id.completeAccountFragment)
            }
        }
    }

    override fun createObserver() {
    }
    companion object {
        private const val ARG_POSITION = "arg_position"
        fun newInstance(position: Int): MobileVerifyFragment {
            return MobileVerifyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }
}