package com.walisport.module.setting.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.common.utils.ext.DimensionExt.px2dp
import com.walisport.lib.common.utils.ext.DimensionExt.px2sp
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.data.BackgroundViewModel
import com.walisport.module.setting.databinding.FragmentBackgroundBinding
import com.walisport.module.setting.databinding.TittleBarBackgroundBinding
import kotlin.reflect.KClass

/**
 * 背景设置
 */

class BackgroundFragment : BaseFragment<BackgroundViewModel, FragmentBackgroundBinding>() {
    override val vbClass: KClass<FragmentBackgroundBinding> = FragmentBackgroundBinding::class
    override val vmClass: KClass<BackgroundViewModel> = BackgroundViewModel::class


    companion object {
        const val SKIN_CLASSIC = "CLASSIC"           //经典
        const val SKIN_BLACK_BLUE = "BLACK_BLUE"     //黑蓝
        const val SKIN_BLACK_GREEN = "BLACK_GREEN"   //黑绿
        const val SKIN_BLACK_RED = "BLACK_RED"       //黑红
        const val SKIN_WHITE_BLUE = "WHITE_BLUE"     //白蓝
        const val SKIN_WHITE_GREEN = "WHITE_GREEN"   //白绿
    }

    override fun initView(savedInstanceState: Bundle?) {
        val skinType = mViewModel.getSkinType()
        changeAppSkin(skinType)
        val binding = TittleBarBackgroundBinding.inflate(LayoutInflater.from(context), mBinding.root, false)
        binding.barroot.layoutParams.width = resources.displayMetrics.widthPixels-(20.dp2px)//获取屏幕高度
        mBinding.titleBar.loadDynamicsTitleBar(binding.root)
        binding.apply {
            tvBack.clickNoRepeat {  findNavController().navigateUp() }
            tvTitleRight.clickNoRepeat {  findNavController().navigateUp() }
        }
    }

    override fun initListener() {
        mBinding.layClassic.clickNoRepeat {
            mViewModel.setSkinType(SKIN_CLASSIC)
        }
        mBinding.layBlackBlue.clickNoRepeat {
            mViewModel.setSkinType(SKIN_BLACK_BLUE)
        }
        mBinding.layBlackRed.clickNoRepeat {
            mViewModel.setSkinType(SKIN_BLACK_RED)
        }
        mBinding.layBlackGreen.clickNoRepeat {
            mViewModel.setSkinType(SKIN_BLACK_GREEN)
        }
        mBinding.layWhiteGreen.clickNoRepeat {
            mViewModel.setSkinType(SKIN_WHITE_GREEN)
        }
        mBinding.layWhiteBlue.clickNoRepeat {
            mViewModel.setSkinType(SKIN_WHITE_BLUE)
        }
    }

    override fun createObserver() {
        mViewModel.skinType.observe(this) {
            changeAppSkin(it)
        }
    }

    private fun changeAppSkin(type: String) {
        if (type == "") return
        mBinding.radioClassic.isChecked = false
        mBinding.radioBlackBlue.isChecked = false
        mBinding.radioBlackRed.isChecked = false
        mBinding.radioBlackGreen.isChecked = false
        mBinding.radioWhiteGreen.isChecked = false
        mBinding.radioWhiteBlue.isChecked = false
        when (type) {
            SKIN_CLASSIC -> mBinding.radioClassic.isChecked = true
            SKIN_BLACK_BLUE -> mBinding.radioBlackBlue.isChecked = true
            SKIN_BLACK_GREEN -> mBinding.radioBlackGreen.isChecked = true
            SKIN_BLACK_RED -> mBinding.radioBlackRed.isChecked = true
            SKIN_WHITE_BLUE -> mBinding.radioWhiteBlue.isChecked = true
            SKIN_WHITE_GREEN -> mBinding.radioWhiteGreen.isChecked = true
        }
    }
}