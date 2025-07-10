package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import com.walisport.module.setting.ui.viewmodel.LanguageViewModel
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 语言设置
 */

class LanguageFragment : BaseFragment<LanguageViewModel, FragmentLanguageBinding>() {

    override val vbClass: KClass<FragmentLanguageBinding> = FragmentLanguageBinding::class
    override val vmClass: KClass<LanguageViewModel> = LanguageViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_language_set, {
            findNavController().navigateUp()
        })
    }

    override fun initListener() {
        mBinding.languageSimple.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_SIMPLE)
        }
        mBinding.languageEnglish.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_ENGLISH)
        }
        mBinding.languageIndonesian.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_ID)
        }
        mBinding.languagePortuguese.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_PT)
        }
    }

    private fun changeLanguageType(type: LanguageType) {
        mBinding.radioSimple.isSelected = type == LanguageType.LANGUAGE_SIMPLE
        mBinding.radioEnglish.isSelected = type == LanguageType.LANGUAGE_ENGLISH
        mBinding.radioIndonesian.isSelected = type == LanguageType.LANGUAGE_ID
        mBinding.radioPortuguese.isSelected = type == LanguageType.LANGUAGE_PT
    }

    override fun createObserver() {
        mViewModel.languageType.observe(viewLifecycleOwner) {
            changeLanguageType(it)
        }
    }

    override fun onDestroy() {
        if (!mViewModel.forceUpdate) {
            mViewModel.saveLanguageType()
        }
        super.onDestroy()
    }
}