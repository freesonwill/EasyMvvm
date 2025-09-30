package com.walisport.module.setting.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.log.Utils
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.setting.BuildConfig
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentSettingBinding
import com.walisport.module.setting.ui.dialog.OddsDisplayDialogFragment
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 设置界面
 */

class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    override val vbClass: KClass<FragmentSettingBinding> = FragmentSettingBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.setting, {
            findNavController().navigateUp()
        })
        if (BuildConfig.BUILD_TYPE == "debug" || BuildConfig.BUILD_TYPE == "qatest") {
            mBinding.tvVersion.isVisible = true
            val appGame = Utils.getApp()
            val pi = appGame.packageManager.getPackageInfo(appGame.packageName, 0)
            mBinding.tvVersion.text =
                "ver.${pi.versionName}_${arch.cayenne.lib.common.BuildConfig.BUILD_TIME}"
        }
        mBinding.tvSetMobile.text = ""
        mBinding.tvSetUser.text = mViewModel.getUserID()
        mBinding.root.touchBackPressed()
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
        mBinding.settingBindMobile.clickNoRepeat {

        }
        mBinding.settingUser.clickNoRepeat {

        }
        mBinding.ivCopy.clickNoRepeat {
            copyToClipboard(mBinding.tvSetUser.text as String?) {
                showToast(R.string.tip_copy_suc.getString())
            }
        }
        mBinding.settingAbout.clickNoRepeat {
            navigate(R.id.action_settingFragment_to_aboutFragment)
        }
    }

    override suspend fun createObserver() {
        mViewModel.displayType.observe(viewLifecycleOwner) { value ->
            mBinding.tvDisplay.text = getSkinnableOddsString(value)
        }
        mViewModel.language.observe(viewLifecycleOwner) { value ->
            mBinding.tvLanguageType.text = getSkinnableLanguageString(value)
            mViewModel.displayType.value?.let { oddsType ->
                mBinding.tvDisplay.text = getSkinnableOddsString(oddsType)
            }
        }
    }

    private fun showOddsDisplayDialog() {
        OddsDisplayDialogFragment().apply {
            val language = mViewModel.getLanguageType()
            val epTips = getSkinnableTipString(OddsDisplayEnum.EU)
            val hkTips = getSkinnableTipString(OddsDisplayEnum.HK)
            arguments = Bundle().apply {
                putString(lang, language.value)
                putString(epTip, epTips)
                putString(hkTip, hkTips)
            }
            setOnItemClickListener(object : OddsDisplayDialogFragment.OnClickListener {
                override fun onClickEP() {
                    mBinding.tvDisplay.text = getSkinnableOddsString(OddsDisplayEnum.EU)
                }

                override fun onClickHK() {
                    mBinding.tvDisplay.text = getSkinnableOddsString(OddsDisplayEnum.HK)
                }
            })
        }.show(childFragmentManager)
    }

    private fun getSkinnableOddsString(oddsType: OddsDisplayEnum): String {
        return if (oddsType == OddsDisplayEnum.EU) {
            SkinnableResourceManager.getString(
                requireContext(),
                R.string.menu_europe,
                mViewModel.getLanguage()
            )
        } else {
            SkinnableResourceManager.getString(
                requireContext(),
                R.string.menu_hk,
                mViewModel.getLanguage()
            )
        }
    }

    private fun getSkinnableTipString(oddsType: OddsDisplayEnum): String {
        return if (oddsType == OddsDisplayEnum.EU) {
            SkinnableResourceManager.getString(
                requireContext(),
                R.string.display_odds_ben,
                mViewModel.getLanguage()
            )
        } else {
            SkinnableResourceManager.getString(
                requireContext(),
                R.string.display_odds_not,
                mViewModel.getLanguage()
            )
        }
    }

    private fun getSkinnableLanguageString(type: LanguageType): String {
        return when (type) {
            LanguageType.LANGUAGE_ENGLISH -> SkinnableResourceManager.getString(
                requireContext(),
                R.string.menu_language_english,
                mViewModel.getLanguage()
            )

            LanguageType.LANGUAGE_PT -> SkinnableResourceManager.getString(
                requireContext(),
                R.string.menu_language_portugal,
                mViewModel.getLanguage()
            )

            LanguageType.LANGUAGE_ID -> SkinnableResourceManager.getString(
                requireContext(),
                R.string.menu_language_indonesia,
                mViewModel.getLanguage()
            )

            else -> SkinnableResourceManager.getString(
                requireContext(),
                R.string.menu_language_simple,
                mViewModel.getLanguage()
            )
        }
    }
}