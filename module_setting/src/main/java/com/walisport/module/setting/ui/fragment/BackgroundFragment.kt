package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarColorExt
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.databinding.FragmentBackgroundBinding
import com.walisport.module.setting.databinding.TitleBarBackgroundBinding
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 背景设置
 */

class BackgroundFragment : BaseFragment<SettingViewModel, FragmentBackgroundBinding>() {

    override val vbClass: KClass<FragmentBackgroundBinding> = FragmentBackgroundBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class
    private var skinType: String = ""
    private var skinOld: String = ""
    private var defaultImmColor: Int = 0
    private var immColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        skinType = mViewModel.getSkinType()
        skinOld = skinType
        changeSkinType(skinType)
        val binding =
            TitleBarBackgroundBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        mBinding.titleBar.loadDynamicsTitleBar(binding.root, null)
        binding.apply {
            barRoot.layoutParams.width = resources.displayMetrics.widthPixels - 20.dp2px
            //点击返回，使用变动前的皮肤
            tvBack.clickNoRepeat {
                mViewModel.setSkinRecord(skinOld)
                findNavController().navigateUp()
            }
            //点击确认，使用变动后的皮肤
            tvTitleRight.clickNoRepeat {
                defaultImmColor = immColor
                mViewModel.setSkinRecord(skinType)
                findNavController().navigateUp()
            }
        }
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llConttnet)
        super.onStart()
    }

    override fun initListener() {
        mBinding.layBlackGreen.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_GREEN.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
        mBinding.layWhiteGreen.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_GREEN.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
    }

    private fun setImmColor(type: String) {
        immColor = immersionBarColorExt(type)
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(skinType)
        setStatusBar(StatusBarConfig, mBinding.llConttnet)
    }

    override suspend fun createObserver() {
        mViewModel.skinType.observe(viewLifecycleOwner) {
            changeSkinType(it)
        }
    }

    private fun changeSkinType(type: String) {
        if ("" == type)
            return
        mBinding.radioBlackGreen.isSelected = false
        mBinding.radioWhiteGreen.isSelected = false
        when (type) {
            SkinType.SKIN_BLACK_GREEN.value -> mBinding.radioBlackGreen.isSelected = true
            SkinType.SKIN_WHITE_GREEN.value -> mBinding.radioWhiteGreen.isSelected = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarConfig.statusBarColor = defaultImmColor
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}