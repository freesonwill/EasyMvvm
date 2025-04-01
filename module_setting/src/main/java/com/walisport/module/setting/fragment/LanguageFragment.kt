package com.walisport.module.setting.fragment

import android.os.Bundle
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.setting.data.LanguageViewModel
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 语言设置
 */

class LanguageFragment : BaseFragment<LanguageViewModel, FragmentLanguageBinding>() {

    override val mBinding: FragmentLanguageBinding by viewBind()
    override val mViewModel: LanguageViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {
    }
}