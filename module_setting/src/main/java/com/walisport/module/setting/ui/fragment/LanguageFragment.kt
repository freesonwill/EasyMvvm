package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageType
import com.walisport.module.setting.ui.viewmodel.LanguageViewModel
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import kotlin.reflect.KClass

/**
 * 语言设置
 */

class LanguageFragment : BaseFragment<LanguageViewModel, FragmentLanguageBinding>() {

    override val vbClass: KClass<FragmentLanguageBinding> = FragmentLanguageBinding::class
    override val vmClass: KClass<LanguageViewModel> = LanguageViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_language_set.getString(), {
            findNavController().navigateUp()
        })
        val languageType = mViewModel.getLanguageType()
        changeLanguageType(languageType)
    }

    override fun initListener() {
        mBinding.languageSimple.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_SIMPLE)
        }
        mBinding.languageTradition.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_TRADITION)
        }
        mBinding.languageEnglish.clickNoRepeat {
            mViewModel.setLanguageType(LanguageType.LANGUAGE_ENGLISH)
        }
    }

    private fun changeLanguageType(type: String) {
        if ("" == type) return
        mBinding.radioSimple.isSelected = false
        mBinding.radioTraditional.isSelected = false
        mBinding.radioEnglish.isSelected = false
        when (type) {
            LanguageType.LANGUAGE_SIMPLE.value -> mBinding.radioSimple.isSelected = true
            LanguageType.LANGUAGE_TRADITION.value -> mBinding.radioTraditional.isSelected = true
            LanguageType.LANGUAGE_ENGLISH.value -> mBinding.radioEnglish.isSelected = true
        }
    }

    override fun createObserver() {
        mViewModel.languageType.observe(this) {
            changeLanguageType(it)
        }
    }
}