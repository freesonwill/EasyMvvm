package com.walisport.module.setting.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib_base.ui.BaseFragment
import com.walisport.lib_base.ui.viewBind
import com.walisport.module.setting.R
import com.walisport.module.setting.data.SettingViewModel
import com.walisport.module.setting.databinding.FragmentSettingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 设置界面
 */

class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    override val mBinding: FragmentSettingBinding by viewBind()
    override val mViewModel: SettingViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.settingOdds.setOnClickListener {

        }
        mBinding.settingNotice.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_noticedFragment)
        }
        mBinding.settingBg.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_backgroundFragment)
        }
        mBinding.settingLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_languageFragment)
        }
    }

    override fun createObserver() {
    }
}