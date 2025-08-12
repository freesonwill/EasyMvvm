package com.walisport.module.login.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentEmailVerifyBinding
import com.walisport.module.login.databinding.FragmentIdentityVerifyBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/16 下午4:06
 * @description:
 */
class EMailVerifyFragment: BaseFragment<EmptyViewModel, FragmentEmailVerifyBinding>() {
    override val vbClass: KClass<FragmentEmailVerifyBinding> = FragmentEmailVerifyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with (mBinding) {
            btnNext.clickNoRepeat {
                // Navigate to the next fragment, e.g., CompleteAccountFragment
                navigate(R.id.completeAccountFragment)
            }
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
    companion object {
        private const val ARG_POSITION = "arg_position"
        fun newInstance(position: Int): EMailVerifyFragment {
            return EMailVerifyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }
}