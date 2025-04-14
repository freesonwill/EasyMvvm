package com.walisport.module.setting.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.data.BackgroundViewModel
import com.walisport.module.setting.data.SkinType
import com.walisport.module.setting.databinding.FragmentBackgroundBinding
import com.walisport.module.setting.databinding.TittleBarBackgroundBinding
import kotlin.reflect.KClass

/**
 * 背景设置
 */

class BackgroundFragment : BaseFragment<BackgroundViewModel, FragmentBackgroundBinding>() {

    override val vbClass: KClass<FragmentBackgroundBinding> = FragmentBackgroundBinding::class
    override val vmClass: KClass<BackgroundViewModel> = BackgroundViewModel::class
    private var skinType: String = "CLASSIC"

    override fun initView(savedInstanceState: Bundle?) {
        skinType = mViewModel.getSkinData()
        changeSkinType(skinType)
        val binding = TittleBarBackgroundBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        binding.barRoot.layoutParams.width = resources.displayMetrics.widthPixels - 20.dp2px
        mBinding.titleBar.loadDynamicsTitleBar(binding.root)
        binding.apply {
            tvBack.clickNoRepeat {
                findNavController().navigateUp()
            }
            tvTitleRight.clickNoRepeat {
                mViewModel.setSkinData(skinType)
                findNavController().navigateUp()
            }
        }
    }

    override fun initListener() {
        mBinding.layClassic.clickNoRepeat {
            skinType = SkinType.SKIN_CLASSIC.value
            mViewModel.setSkinType(SkinType.SKIN_CLASSIC)
        }
        mBinding.layBlackBlue.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_BLUE.value
            mViewModel.setSkinType(SkinType.SKIN_BLACK_BLUE)
        }
        mBinding.layBlackRed.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_RED.value
            mViewModel.setSkinType(SkinType.SKIN_BLACK_RED)
        }
        mBinding.layBlackGreen.clickNoRepeat {
            skinType = SkinType.SKIN_BLACK_GREEN.value
            mViewModel.setSkinType(SkinType.SKIN_BLACK_GREEN)
        }
        mBinding.layWhiteGreen.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_GREEN.value
            mViewModel.setSkinType(SkinType.SKIN_WHITE_GREEN)
        }
        mBinding.layWhiteBlue.clickNoRepeat {
            skinType = SkinType.SKIN_WHITE_BLUE.value
            mViewModel.setSkinType(SkinType.SKIN_WHITE_BLUE)
        }
    }

    override fun createObserver() {
        mViewModel.skinType.observe(this) {
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
}