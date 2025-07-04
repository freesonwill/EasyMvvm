package com.walisport.module.setting.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.setting.R
import com.walisport.module.setting.data.OddsDisplayEnum
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import com.walisport.module.setting.databinding.FragmentSettingBinding
import com.walisport.module.setting.ui.dialog.OddsDisplayDialogFragment
import kotlin.reflect.KClass

/**
 * 设置界面
 */

class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    override val vbClass: KClass<FragmentSettingBinding> = FragmentSettingBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    private var skinType: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.setting, {
            findNavController().navigateUp()
        })
        //设置皮肤
        skinType = mViewModel.getSkinType()
        mViewModel.setSkinType(skinType)
        //设置语言
        val lang = mViewModel.getSkinnableLanguage(mBinding.root.context)
        mBinding.tvLanguageType.text = lang
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

    override fun createObserver() {
        mViewModel.displayType.observe(viewLifecycleOwner) { value ->
            mBinding.tvDisplay.text = getSkinnableOddsString(value)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            //语言类型
            val lang = mViewModel.getSkinnableLanguage(mBinding.root.context)
            mBinding.tvLanguageType.text = lang
            mBinding.tvDisplay.text = getSkinnableOddsString(mViewModel.displayType.value ?: OddsDisplayEnum.EU)
            //皮肤设置
            skinType = mViewModel.getSkinType()
            mViewModel.setSkinType(skinType)
        }
    }

    private fun showOddsDisplayDialog() {
        OddsDisplayDialogFragment().show(childFragmentManager)
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
}