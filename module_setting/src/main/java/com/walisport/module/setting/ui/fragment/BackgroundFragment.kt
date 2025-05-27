package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.StatusBarMode
import arch.cayenne.lib.base.data.model.SkinType
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
    private var skinType: String = "CLASSIC"
    private var defaultImmColor :Int = 0
    private var immColor :Int = 0
    override fun initView(savedInstanceState: Bundle?) {
        defaultImmColor =getStatusBarColor()
        skinType = mViewModel.getSkinData()
        changeSkinType(skinType)
        val binding = TitleBarBackgroundBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        binding.barRoot.layoutParams.width = resources.displayMetrics.widthPixels - 20.dp2px
        mBinding.titleBar.loadDynamicsTitleBar(binding.root, null)
        binding.apply {
            tvBack.clickNoRepeat {
                findNavController().navigateUp()
            }
            tvTitleRight.clickNoRepeat {
                defaultImmColor = immColor
                mViewModel.setSkinData(skinType)
                findNavController().navigateUp()
            }
        }
    }

    override fun onStart() {
        StatusBarConfig.statusBarType =StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig,mBinding.root)
        super.onStart()
    }

    override fun initListener() {
        mBinding.layClassic.clickNoRepeat {
            skinType = SkinType.SKIN_CLASSIC.value
            mViewModel.setSkinType(SkinType.SKIN_CLASSIC)
            setImmColor(SkinType.SKIN_CLASSIC.value)
        }
        mBinding.layBlackBlue.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_BLUE.value
            mViewModel.setSkinType(SkinType.SKIN_BLACK_BLUE)
            setImmColor(SkinType.SKIN_BLACK_BLUE.value)
        }
        mBinding.layBlackRed.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_RED.value
            mViewModel.setSkinType(SkinType.SKIN_BLACK_RED)
            setImmColor(SkinType.SKIN_BLACK_RED.value)
        }
        mBinding.layBlackGreen.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_GREEN.value
            mViewModel.setSkinType(SkinType.SKIN_BLACK_GREEN)
            setImmColor(SkinType.SKIN_BLACK_GREEN.value)
        }
        mBinding.layWhiteGreen.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_GREEN.value
            mViewModel.setSkinType(SkinType.SKIN_WHITE_GREEN)
            setImmColor(SkinType.SKIN_WHITE_GREEN.value)
        }
        mBinding.layWhiteBlue.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_BLUE.value
            mViewModel.setSkinType(SkinType.SKIN_WHITE_BLUE)
            setImmColor(SkinType.SKIN_WHITE_BLUE.value)
        }
    }
    private fun setImmColor(type: String){
        immColor = immersionBarColorExt(type)
        StatusBarConfig.statusBarColor =immColor
        StatusBarConfig.statusBarType =StatusBarMode.DRAW_BEHIND
        StatusBarConfig.statusBarDarkFont =  immersionBarSkinTypeExt(skinType)
        setStatusBar(StatusBarConfig,mBinding.root)
    }

    override fun createObserver() {
        mViewModel.skinType.observe(viewLifecycleOwner) {
            changeSkinType(it)
        }
    }

    private fun changeSkinType(type: String) {
        if ("" == type) return
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
        StatusBarConfig.statusBarColor =defaultImmColor
        StatusBarConfig.statusBarDarkFont =  immersionBarSkinTypeExt(mViewModel.getSkinData())
        StatusBarConfig.statusBarType =StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig,mBinding.root)
    }
}