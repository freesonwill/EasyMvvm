package com.walisport.module.login.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.login.R
import com.walisport.module.login.databinding.FragmentNewMobileVerifyBinding
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/8/12 下午4:33
 * @description:
 */
class NewMobileVerifyFragment: BaseFragment<EmptyViewModel, FragmentNewMobileVerifyBinding>() {
    override val vbClass: KClass<FragmentNewMobileVerifyBinding> = FragmentNewMobileVerifyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with (mBinding) {
            titleBar.loadGeneralTitleBar("", {
                findNavController().navigateUp()
            })
            val statusBarHeight =
                ViewCompat.getRootWindowInsets(requireView())
                    ?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
            val params = mBinding.titleBar.layoutParams as ViewGroup.MarginLayoutParams
            // 設定 topMargin
            params.topMargin = statusBarHeight
            mBinding.titleBar.layoutParams = params
        }
    }

    override fun initListener() {
        with(mBinding) {
            btnNext.clickNoRepeat {
                navigate(R.id.completeAccountFragment)
            }
        }
    }

    override suspend fun createObserver() {
    }
    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.ivLogo)
        super.onStart()
    }
}