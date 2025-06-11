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
import com.walisport.module.setting.ui.viewmodel.BackgroundViewModel
import com.walisport.module.setting.databinding.FragmentBackgroundBinding
import com.walisport.module.setting.databinding.TitleBarBackgroundBinding
import kotlin.reflect.KClass

/**
 * 背景设置
 */

class BackgroundFragment : BaseFragment<BackgroundViewModel, FragmentBackgroundBinding>() {

    override val vbClass: KClass<FragmentBackgroundBinding> = FragmentBackgroundBinding::class
    override val vmClass: KClass<BackgroundViewModel> = BackgroundViewModel::class
    private var skinType: String = ""
    private var skinOld: String = ""
    private var defaultImmColor: Int = 0
    private var immColor: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor = getStatusBarColor()
        skinType = mViewModel.getSkinData()
        skinOld = skinType
        changeSkinType(skinType)
        val binding = TitleBarBackgroundBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        mBinding.titleBar.loadDynamicsTitleBar(binding.root, null)
        binding.apply {
            barRoot.layoutParams.width = resources.displayMetrics.widthPixels - 20.dp2px
            //点击返回，如果存在变动就恢复变动前的皮肤
            tvBack.clickNoRepeat {
                if (skinOld != skinType) {
                    mViewModel.setSkinData(skinOld)
                }
                findNavController().navigateUp()
            }
            //点击确认，如果存在变动就使用变动后的皮肤
            tvTitleRight.clickNoRepeat {
                defaultImmColor = immColor
                mViewModel.setSkinData(skinType)
                findNavController().navigateUp()
            }
        }
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig, mBinding.root)
        super.onStart()
    }

    override fun initListener() {
        mBinding.layClassic.clickNoRepeat {
            skinType = SkinType.SKIN_CLASSIC.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
        mBinding.layBlackBlue.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_BLUE.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
        mBinding.layBlackGreen.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_GREEN.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
        mBinding.layBlackRed.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_RED.value
            mViewModel.setSkinType(skinType)
            setImmColor(skinType)
        }
        mBinding.layWhiteBlue.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_BLUE.value
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
        StatusBarConfig.statusBarColor = immColor
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(skinType)
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    override fun createObserver() {
        mViewModel.skinType.observe(viewLifecycleOwner) {
            changeSkinType(it)
        }
    }

    private fun changeSkinType(type: String) {
        if ("" == type)
            return
        mBinding.radioClassic.isSelected = false
        mBinding.radioBlackBlue.isSelected = false
        mBinding.radioBlackRed.isSelected = false
        mBinding.radioBlackGreen.isSelected = false
        mBinding.radioWhiteGreen.isSelected = false
        mBinding.radioWhiteBlue.isSelected = false
        when (type) {
            SkinType.SKIN_CLASSIC.value -> mBinding.radioClassic.isSelected = true
            SkinType.SKIN_BLACK_BLUE.value -> mBinding.radioBlackBlue.isSelected = true
            SkinType.SKIN_BLACK_GREEN.value -> mBinding.radioBlackGreen.isSelected = true
            SkinType.SKIN_BLACK_RED.value -> mBinding.radioBlackRed.isSelected = true
            SkinType.SKIN_WHITE_BLUE.value -> mBinding.radioWhiteBlue.isSelected = true
            SkinType.SKIN_WHITE_GREEN.value -> mBinding.radioWhiteGreen.isSelected = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarConfig.statusBarColor = defaultImmColor
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinData())
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}