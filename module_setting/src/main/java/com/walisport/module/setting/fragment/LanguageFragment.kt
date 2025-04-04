package com.walisport.module.setting.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageViewModel
import com.walisport.module.setting.data.NoticeViewModel
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import com.walisport.module.setting.databinding.FragmentNoticeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

/**
 * 语言设置
 */

class LanguageFragment : BaseFragment<LanguageViewModel, FragmentLanguageBinding>() {
    override val vbClass: KClass<FragmentLanguageBinding> = FragmentLanguageBinding::class
    override val vmClass: KClass<LanguageViewModel> = LanguageViewModel::class

    companion object {
        const val TYPE_SIMPLE = 0      //简体
        const val TYPE_TRADITION = 1   //繁体
        const val TYPE_ENGLISH = 2     //英文
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_language_set.getString()) {
            findNavController().navigateUp()
        }
        val languageType = mViewModel.getLanguageType()
        if ("SIMPLE" == languageType) {
            mBinding.radioSimple.isChecked = true
            mBinding.radioTraditional.isChecked = false
            mBinding.radioEnglish.isChecked = false
        } else if ("TRADITION" == languageType) {
            mBinding.radioSimple.isChecked = false
            mBinding.radioTraditional.isChecked = true
            mBinding.radioEnglish.isChecked = false
        } else {
            mBinding.radioSimple.isChecked = false
            mBinding.radioTraditional.isChecked = false
            mBinding.radioEnglish.isChecked = true
        }
    }

    override fun initListener() {
        mBinding.radioSimple.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setRadioButtonChecked(TYPE_SIMPLE)
            }
        }
        mBinding.radioTraditional.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setRadioButtonChecked(TYPE_TRADITION)
            }
        }
        mBinding.radioEnglish.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setRadioButtonChecked(TYPE_ENGLISH)
            }
        }
        mBinding.languageSimple.clickNoRepeat {
            setRadioButtonChecked(TYPE_SIMPLE)
        }
        mBinding.languageTradition.clickNoRepeat {
            setRadioButtonChecked(TYPE_TRADITION)
        }
        mBinding.languageEnglish.clickNoRepeat {
            setRadioButtonChecked(TYPE_ENGLISH)
        }
    }

    private fun setRadioButtonChecked(type: Int) {
        when (type) {
            TYPE_SIMPLE -> {
                mBinding.radioSimple.isChecked = true
                mBinding.radioTraditional.isChecked = false
                mBinding.radioEnglish.isChecked = false
                mViewModel.setLanguageType("SIMPLE")
            }

            TYPE_TRADITION -> {
                mBinding.radioSimple.isChecked = false
                mBinding.radioTraditional.isChecked = true
                mBinding.radioEnglish.isChecked = false
                mViewModel.setLanguageType("TRADITION")
            }

            TYPE_ENGLISH -> {
                mBinding.radioSimple.isChecked = false
                mBinding.radioTraditional.isChecked = false
                mBinding.radioEnglish.isChecked = true
                mViewModel.setLanguageType("ENGLISH")
            }
        }
    }

    override fun createObserver() {

    }
}