package com.walisport.module.setting.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageType
import com.walisport.module.setting.data.SettingViewModel
import com.walisport.module.setting.databinding.FragmentSettingBinding
import com.walisport.module.setting.dialog.OddsDisplayDialog
import kotlin.reflect.KClass

/**
 * 设置界面
 */

class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    override val vbClass: KClass<FragmentSettingBinding> = FragmentSettingBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        //加载皮肤方案，后面需要转移到Splash启动界面
        mViewModel.loadMyAppSkin()
        //加载语言类型，后面需要转移到Splash启动界面
        val languageType = mViewModel.getLanguageType()
        when (languageType) {
            LanguageType.LANGUAGE_SIMPLE.value -> mBinding.tvLanguageType.text =
                getString(R.string.menu_language_simple)

            LanguageType.LANGUAGE_TRADITION.value -> mBinding.tvLanguageType.text =
                getString(R.string.menu_language_traditional)

            LanguageType.LANGUAGE_ENGLISH.value -> mBinding.tvLanguageType.text =
                getString(R.string.menu_language_english)
        }
        //加载赔率显示方式设置
        val displayType = mViewModel.getDisplayType()
        if ("HK" == displayType) {
            mBinding.tvDisplay.text = getString(R.string.menu_hk)
        } else {
            mBinding.tvDisplay.text = getString(R.string.menu_europe)
        }
    }

    override fun initListener() {
        mBinding.titleBar.loadGeneralTitleBar(R.string.setting.getString(), null, {
            findNavController().navigateUp()
        })
        mBinding.settingOdds.clickNoRepeat {
            showOddsDisplayDialog()
        }
        mBinding.settingNotice.clickNoRepeat {
            navigate(R.id.action_settingFragment_to_noticedFragment)
        }
        mBinding.settingBg.clickNoRepeat {
            navigate(R.id.action_settingFragment_to_backgroundFragment)
        }
        mBinding.settingLanguage.clickNoRepeat {
            navigate(R.id.action_settingFragment_to_languageFragment)
        }
    }

    override fun createObserver() {

    }

    private fun showOddsDisplayDialog() {
        val displayType = mViewModel.getDisplayType()
        val fragmentManager = requireActivity().supportFragmentManager
        OddsDisplayDialog().apply {
            arguments = Bundle().apply {
                putString(bundle, displayType)
            }
            setOnItemClickListener(object : OddsDisplayDialog.OnClickListener {
                override fun onClickEP() {
                    mViewModel.setDisplayType("EP")
                    mBinding.tvDisplay.text = getString(R.string.menu_europe)
                    //延迟关闭弹窗防止RadioButton状态尚未改变就关闭
                    mBinding.tvDisplay.postDelayed({
                        dialog?.dismiss()
                    }, 300)
                }

                override fun onClickHK() {
                    mViewModel.setDisplayType("HK")
                    mBinding.tvDisplay.text = getString(R.string.menu_hk)
                    mBinding.tvDisplay.postDelayed({
                        dialog?.dismiss()
                    }, 300)
                }

                override fun onClickClose() {
                    mBinding.titleBar.postDelayed({
                        dialog?.dismiss()
                    }, 300)
                }
            })
        }.show(fragmentManager)
    }
}