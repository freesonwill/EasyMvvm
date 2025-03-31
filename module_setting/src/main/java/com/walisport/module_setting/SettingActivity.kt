package com.walisport.module_setting

import android.os.Bundle
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.launch
import com.walisport.lib_base.ui.viewBind
import com.walisport.lib_base.utils.LogUtilsExt.loge
import com.walisport.module_setting.data.SettingViewModel
import com.walisport.module_setting.databinding.ActivitySettingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 瓦力体育设置界面
 */

class SettingActivity : BaseActivity<SettingViewModel, ActivitySettingBinding>() {

    override val mBinding: ActivitySettingBinding by viewBind()
    override val mViewModel: SettingViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.relSettingOdds.setOnClickListener {
            mViewModel.setSkinType("BLUE")
        }
        mBinding.relSettingNotice.setOnClickListener {
            mViewModel.setSkinType("RED")
        }
        mBinding.relSettingBg.setOnClickListener {

        }
        mBinding.relSettingLanguage.setOnClickListener {

        }
    }

    override fun createObserver() {
        launch{
            mViewModel.skinType.collect{
                //监听
                "==========${mViewModel.skinType.value}".loge("测试")
            }
        }
    }

}