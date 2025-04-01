package com.walisport.module_setting.fragment

import android.os.Bundle
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module_setting.data.BackgroundViewModel
import com.walisport.module_setting.databinding.FragmentBackgroundBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 背景设置
 */

class BackgroundFragment: BaseFragment<BackgroundViewModel, FragmentBackgroundBinding>() {

    override val mBinding: FragmentBackgroundBinding by viewBind()
    override val mViewModel: BackgroundViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {
    }
}