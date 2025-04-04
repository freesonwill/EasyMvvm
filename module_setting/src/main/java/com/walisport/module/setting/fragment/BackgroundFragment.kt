package com.walisport.module.setting.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.ResourceExt.getString
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.setting.R
import com.walisport.module.setting.data.BackgroundViewModel
import com.walisport.module.setting.databinding.FragmentBackgroundBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 背景设置
 */

class BackgroundFragment : BaseFragment<BackgroundViewModel, FragmentBackgroundBinding>() {

    override val mBinding: FragmentBackgroundBinding by viewBind()
    override val mViewModel: BackgroundViewModel by viewModel()

    companion object {
        const val SKIN_CLASS = "CLASS"               //经典
        const val SKIN_BLACK_BLUE = "BLACK_BLUE"     //黑蓝
        const val SKIN_BLACK_GREEN = "BLACK_GREEN"   //黑绿
        const val SKIN_BLACK_RED = "BLACK_RED"       //黑红
        const val SKIN_WHITE_BLUE = "WHITE_BLUE"     //白蓝
        const val SKIN_WHITE_GREEN = "WHITE_GREEN"   //白绿
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.titleBar.loadBackgroundTitleBar(
            R.string.menu_background_set.getString(),
            R.string.cancel.getString(),
            R.string.confirm.getString(),
            {
                findNavController().navigateUp()
            },
            {//确定
                findNavController().navigateUp()
            })
        mBinding.layClass.clickNoRepeat {
            mViewModel.setSkinType(SKIN_CLASS)
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
        mBinding.radioClass.isChecked = false
        mBinding.radioBlackBlue.isChecked = false
        mBinding.radioBlackRed.isChecked = false
        mBinding.radioBlackGreen.isChecked = false
        mBinding.radioWhiteGreen.isChecked = false
        mBinding.radioWhiteBlue.isChecked = false
        when (type) {
            SKIN_CLASS -> mBinding.radioClass.isChecked = true
            SKIN_BLACK_BLUE -> mBinding.radioBlackBlue.isChecked = true
            SKIN_BLACK_GREEN -> mBinding.radioBlackGreen.isChecked = true
            SKIN_BLACK_RED -> mBinding.radioBlackRed.isChecked = true
            SKIN_WHITE_BLUE -> mBinding.radioWhiteBlue.isChecked = true
            SKIN_WHITE_GREEN -> mBinding.radioWhiteGreen.isChecked = true
        }
    }
}