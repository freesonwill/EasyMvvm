package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.data.LanguageType
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import com.walisport.module.setting.databinding.FragmentSettingBinding
import com.walisport.module.setting.ui.dialog.OddsDisplayDialog
import kotlin.reflect.KClass

/**
 * 设置界面
 */

class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    override val vbClass: KClass<FragmentSettingBinding> = FragmentSettingBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    private var skinType: String = ""
    private var oddsType: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.setting.getString(), {
            findNavController().navigateUp()
        })
        //设置皮肤
        skinType = mViewModel.getSkinType()
        mViewModel.setSkinType(skinType)
        //设置语言
        val langType = mViewModel.getLanguageType()
        mBinding.tvLanguageType.text = getLanguage(langType)
        //设置赔率显示方式
        oddsType = mViewModel.getOddsType()
        if (oddsType == 0) {
            mBinding.tvDisplay.text = getString(R.string.menu_europe)
        } else {
            mBinding.tvDisplay.text = getString(R.string.menu_hk)
        }
    }

    override fun initListener() {
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

    override fun createObserver() {}

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val lang = mViewModel.getLanguageType()
            mBinding.tvLanguageType.text = getLanguage(lang)
            val skin = mViewModel.getSkinType()
            if(skin != skinType){
                mViewModel.setSkinType(skin)
            }
        }
    }

    private fun getLanguage(type: String): String {
        return when (type) {
            LanguageType.LANGUAGE_SIMPLE.value -> getString(R.string.menu_language_simple)
            LanguageType.LANGUAGE_PT.value -> getString(R.string.menu_language_portugal)
            LanguageType.LANGUAGE_ID.value -> getString(R.string.menu_language_indonesia)
            else -> getString(R.string.menu_language_english)
        }
    }

    private fun showOddsDisplayDialog() {
        val fragmentManager = requireActivity().supportFragmentManager
        OddsDisplayDialog().apply {
            arguments = Bundle().apply {
                putInt(bundle, oddsType)
            }
            setOnItemClickListener(object : OddsDisplayDialog.OnClickListener {
                override fun onClickEP() {
                    mViewModel.setOddsType(0)
                    mBinding.tvDisplay.text = getString(R.string.menu_europe)
                    mBinding.tvDisplay.postDelayed({//延迟关闭弹窗防止RadioButton状态尚未改变就关闭
                        dialog?.dismiss()
                    }, 300)
                }

                override fun onClickHK() {
                    mViewModel.setOddsType(1)
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