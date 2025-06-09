package com.walisport.module.setting.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageType
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

    private fun changeLanguageType(type: String) {
        if ("" == type)
            return
        var language = "zh"
        mBinding.radioSimple.isSelected = false
        mBinding.radioEnglish.isSelected = false
        mBinding.radioIndonesian.isSelected = false
        mBinding.radioPortuguese.isSelected = false
        when (type) {
            LanguageType.LANGUAGE_SIMPLE.value -> {
                language = Locale.SIMPLIFIED_CHINESE.language
                mBinding.radioSimple.isSelected = true
            }

            LanguageType.LANGUAGE_ENGLISH.value -> {
                language = Locale.ENGLISH.language
                mBinding.radioEnglish.isSelected = true
            }

            LanguageType.LANGUAGE_ID.value -> {
                language = "in"
                mBinding.radioIndonesian.isSelected = true
            }

            LanguageType.LANGUAGE_PT.value -> {
                language = "pt"
                mBinding.radioPortuguese.isSelected = true
            }
        }
        context?.let { updateLanguage(it, language) }
    }

    private fun updateLanguage(ctx: Context, language: String) {
        val configuration = Configuration(ctx.resources.configuration)
        val locale = configuration.locale
        if (locale.language != language) {
            val local = Locale(language)
            Locale.setDefault(local)
            val config = Configuration()
            config.locale = local
            ctx.createConfigurationContext(config)
            ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
            //在不销毁Fragment的情况下更新文字
            val title = mBinding.titleBar.findViewById<SkinnableTextView>(R.id.tv_title_name)
            title.text = getString(R.string.menu_language_set)
            mBinding.tvSimple.text = getString(R.string.menu_language_simple)
            mBinding.tvEnglish.text = getString(R.string.menu_language_english)
            mBinding.tvIndonesia.text = getString(R.string.menu_language_indonesia)
            mBinding.tvPortugal.text = getString(R.string.menu_language_portugal)
        }
    }

    override fun createObserver() {
        mViewModel.languageType.observe(viewLifecycleOwner) {
            changeLanguageType(it)
        }
    }
}