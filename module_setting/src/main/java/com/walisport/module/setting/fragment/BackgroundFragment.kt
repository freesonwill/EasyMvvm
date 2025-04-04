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
        const val SKIN_CLASS = 0         //经典
        const val SKIN_BLACK_BLUE = 1    //黑蓝
        const val SKIN_BLACK_GREEN = 2   //黑绿
        const val SKIN_BLACK_RED = 3     //黑红
        const val SKIN_WHITE_BLUE = 4    //白蓝
        const val SKIN_WHITE_GREEN = 5   //白绿
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
            setRadioButtonChecked(SKIN_CLASS)
        }
        mBinding.layBlackBlue.clickNoRepeat {
            setRadioButtonChecked(SKIN_BLACK_BLUE)
        }
        mBinding.layBlackRed.clickNoRepeat {
            setRadioButtonChecked(SKIN_BLACK_RED)
        }
        mBinding.layBlackGreen.clickNoRepeat {
            setRadioButtonChecked(SKIN_BLACK_GREEN)
        }
        mBinding.layWhiteGreen.clickNoRepeat {
            setRadioButtonChecked(SKIN_WHITE_GREEN)
        }
        mBinding.layWhiteBlue.clickNoRepeat {
            setRadioButtonChecked(SKIN_WHITE_BLUE)
        }
    }

    private fun setRadioButtonChecked(type: Int) {
        when (type) {
            SKIN_CLASS -> {
                mBinding.radioClass.isChecked = true
                mBinding.radioBlackBlue.isChecked = false
                mBinding.radioBlackRed.isChecked = false
                mBinding.radioBlackGreen.isChecked = false
                mBinding.radioWhiteGreen.isChecked = false
                mBinding.radioWhiteBlue.isChecked = false
            }

            SKIN_BLACK_BLUE -> {
                mBinding.radioClass.isChecked = false
                mBinding.radioBlackBlue.isChecked = true
                mBinding.radioBlackRed.isChecked = false
                mBinding.radioBlackGreen.isChecked = false
                mBinding.radioWhiteGreen.isChecked = false
                mBinding.radioWhiteBlue.isChecked = false
            }

            SKIN_BLACK_GREEN -> {
                mBinding.radioClass.isChecked = false
                mBinding.radioBlackBlue.isChecked = false
                mBinding.radioBlackRed.isChecked = false
                mBinding.radioBlackGreen.isChecked = true
                mBinding.radioWhiteGreen.isChecked = false
                mBinding.radioWhiteBlue.isChecked = false
            }

            SKIN_BLACK_RED -> {
                mBinding.radioClass.isChecked = false
                mBinding.radioBlackBlue.isChecked = false
                mBinding.radioBlackRed.isChecked = true
                mBinding.radioBlackGreen.isChecked = false
                mBinding.radioWhiteGreen.isChecked = false
                mBinding.radioWhiteBlue.isChecked = false
            }

            SKIN_WHITE_BLUE -> {
                mBinding.radioClass.isChecked = false
                mBinding.radioBlackBlue.isChecked = false
                mBinding.radioBlackRed.isChecked = false
                mBinding.radioBlackGreen.isChecked = false
                mBinding.radioWhiteGreen.isChecked = false
                mBinding.radioWhiteBlue.isChecked = true
            }

            SKIN_WHITE_GREEN -> {
                mBinding.radioClass.isChecked = false
                mBinding.radioBlackBlue.isChecked = false
                mBinding.radioBlackRed.isChecked = false
                mBinding.radioBlackGreen.isChecked = false
                mBinding.radioWhiteGreen.isChecked = true
                mBinding.radioWhiteBlue.isChecked = false
            }
        }
    }

    override fun createObserver() {
    }
}