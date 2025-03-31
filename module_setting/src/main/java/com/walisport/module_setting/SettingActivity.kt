package com.walisport.module_setting

import android.os.Bundle
import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import com.walisport.module_setting.databinding.ActivitySettingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 瓦力体育设置界面
 */

class SettingActivity : BaseActivity<EmptyViewModel, ActivitySettingBinding>() {
    override val mBinding: ActivitySettingBinding by viewBind()
    override val mViewModel: EmptyViewModel by viewModel()


    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.relSettingOdds.setOnClickListener {

        }
        mBinding.relSettingNotice.setOnClickListener {

        }
        mBinding.relSettingBg.setOnClickListener {

        }
        mBinding.relSettingLanguage.setOnClickListener {

        }
    }

    override fun createObserver() {

    }

}