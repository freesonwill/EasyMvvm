package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import com.walisport.module.setting.ui.viewmodel.LanguageViewModel
import java.util.Locale
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
        mBinding.root.touchBackPressed()
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

    private fun updateLanguage(locale: Locale, v: View) {
        if (v is ViewGroup) {
            v.forEach {
                updateLanguage(locale, it)
            }
        } else if (v is SkinnableTextView) {
            v.updateLanguage(locale)
        }
    }

    override suspend fun createObserver() {
        mViewModel.languageType.observe(viewLifecycleOwner) {
            changeLanguageType(it)
            updateLanguage(Locale(it.value), mBinding.root)
        }
    }

    override fun onPause() {
        if (!mViewModel.forceUpdate) {
            mViewModel.saveLanguageType()
        }
        super.onPause()
    }
}