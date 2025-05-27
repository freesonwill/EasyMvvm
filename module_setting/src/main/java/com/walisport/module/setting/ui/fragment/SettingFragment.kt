package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.StatusBarEnum
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
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
    private var langType: String = "ZH"                            //语言类型

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.setting.getString(), {
            findNavController().navigateUp()
        })
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
                //语言类型，TW-繁体 ZH-简体 EN-英文
                langType = it.lang
                when (langType) {
                    "TW" -> mBinding.tvLanguageType.text =
                        getString(R.string.menu_language_traditional)

                    "EN" -> mBinding.tvLanguageType.text = getString(R.string.menu_language_english)
                    else -> mBinding.tvLanguageType.text = getString(R.string.menu_language_simple)
                }
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

    override fun onStart() {
        StatusBarConfig.statusBarType =StatusBarEnum.TOP_UP
        setStatusBar(StatusBarConfig,mBinding.root)
        super.onStart()
    }
}