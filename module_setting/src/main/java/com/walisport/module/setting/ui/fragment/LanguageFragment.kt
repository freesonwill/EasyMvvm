package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageType
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 语言设置
 */

class LanguageFragment : BaseFragment<SettingViewModel, FragmentLanguageBinding>() {

    override val vbClass: KClass<FragmentLanguageBinding> = FragmentLanguageBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_language_set, {
            findNavController().navigateUp()
        })
        val languageType = mViewModel.getLanguageType()
        changeLanguageType(languageType)
    }

    override fun initListener() {
        mBinding.languageSimple.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_SIMPLE.value)
        }
        mBinding.languageEnglish.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_ENGLISH.value)
        }
        mBinding.languageIndonesian.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_ID.value)
        }
        mBinding.languagePortuguese.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_PT.value)
        }
    }

    private fun changeLanguageType(type: String) {
        if ("" == type)
            return
        mBinding.radioSimple.isSelected = false
        mBinding.radioEnglish.isSelected = false
        mBinding.radioIndonesian.isSelected = false
        mBinding.radioPortuguese.isSelected = false
        when (type) {
            LanguageType.LANGUAGE_SIMPLE.value -> mBinding.radioSimple.isSelected = true
            LanguageType.LANGUAGE_ENGLISH.value -> mBinding.radioEnglish.isSelected = true
            LanguageType.LANGUAGE_ID.value -> mBinding.radioIndonesian.isSelected = true
            LanguageType.LANGUAGE_PT.value -> mBinding.radioPortuguese.isSelected = true
        }
    }

    override fun createObserver() {
        mViewModel.language.observe(viewLifecycleOwner) {
            changeLanguageType(it)
        }
    }
}