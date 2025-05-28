package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
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

    private var oddsType: Int = 0                                  //赔率显示类型
    private var langType: String = "zh-CN"                         //语言类型

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.setting.getString(), {
            findNavController().navigateUp()
        })
        //默认或者无网情况下从记录中获取数据
        langType = mViewModel.getLanguageType()
        mBinding.tvLanguageType.text = getLanguage(langType)
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

    override fun initData() {
        mViewModel.getSystemSetting()
    }

    override fun createObserver() {
        mViewModel.systemSetting.observe(viewLifecycleOwner) {
            it?.let {
                //赔率类型, 0-欧盘 1-香港盘
                oddsType = it.oddType
                if (oddsType == 0) {
                    mBinding.tvDisplay.text = getString(R.string.menu_europe)
                } else {
                    mBinding.tvDisplay.text = getString(R.string.menu_hk)
                }
                mViewModel.setOddsType(oddsType)
                //语言类型 zh-CN：简体中文  en-US：英文  id-ID：印尼语  pt-PT：葡萄牙语
                langType = it.lang
                mBinding.tvLanguageType.text = getLanguage(langType)
                mViewModel.setLanguageType(langType)
                //系统通知-进球
                val sys = it.systemGoal
                mViewModel.setSystemGoal(sys.betMatch, sys.collectMatch, sys.allMatch)
                //系统通知-开赛
                val kick = it.systemKickOff
                mViewModel.setKickGoal(kick.betMatch, kick.collectMatch, kick.allMatch)
                //应用内通知-进球
                val app = it.appGoal
                mViewModel.setAppGoal(app.betMatch, app.collectMatch, app.allMatch)
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
                    mViewModel.updateOddsSetting(0)
                    mBinding.tvDisplay.text = getString(R.string.menu_europe)
                    mBinding.tvDisplay.postDelayed({//延迟关闭弹窗防止RadioButton状态尚未改变就关闭
                        dialog?.dismiss()
                    }, 300)
                }

                override fun onClickHK() {
                    mViewModel.setOddsType(1)
                    mViewModel.updateOddsSetting(1)
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