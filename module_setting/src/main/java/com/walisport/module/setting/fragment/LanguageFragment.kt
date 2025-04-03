package com.walisport.module.setting.fragment

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageViewModel
import com.walisport.module.setting.databinding.FragmentLanguageBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 语言设置
 */

class LanguageFragment : BaseFragment<LanguageViewModel, FragmentLanguageBinding>() {

    override val mBinding: FragmentLanguageBinding by viewBind()
    override val mViewModel: LanguageViewModel by viewModel()

    companion object {
        const val TYPE_SIMPLE = 0      //简体
        const val TYPE_TRADITION = 1   //繁体
        const val TYPE_ENGLISH = 2     //英文
    }

    override fun initView(savedInstanceState: Bundle?) {
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
        mBinding.titleBar.loadGeneralTitleBar(R.string.menu_language_set.getString()) {

        }
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
        mBinding.languageSimple.setOnClickListener {
            setRadioButtonChecked(TYPE_SIMPLE)
        }
        mBinding.languageTradition.setOnClickListener {
            setRadioButtonChecked(TYPE_TRADITION)
        }
        mBinding.languageEnglish.setOnClickListener {
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