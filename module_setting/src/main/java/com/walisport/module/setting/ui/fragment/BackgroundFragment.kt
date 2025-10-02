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
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.setting.R
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
    private var defaultImmColor: Int = 0
    private var immColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        skinType = mViewModel.getSkinType()
        changeSkinType(skinType)
        val bind = TitleBarBackgroundBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        mBinding.titleBar.loadDynamicsTitleBar(bind.root, null)
        bind.apply {
            barRoot.layoutParams.width = resources.displayMetrics.widthPixels - 20.dp2px
            tvTitleName.text = R.string.menu_background_set.getString()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
        }
        mBinding.root.touchBackPressed()
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llConttnet)
        super.onStart()
    }

    override fun initListener() {
        mBinding.layBlackBlue.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_BLUE.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
        mBinding.layWhiteBlue.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_BLUE.value
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
        mBinding.radioBlackBlue.isSelected = false
        mBinding.radioWhiteBlue.isSelected = false
        when (type) {
            SkinType.SKIN_BLACK_BLUE.value -> mBinding.radioBlackBlue.isSelected = true
            SkinType.SKIN_WHITE_BLUE.value -> mBinding.radioWhiteBlue.isSelected = true
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