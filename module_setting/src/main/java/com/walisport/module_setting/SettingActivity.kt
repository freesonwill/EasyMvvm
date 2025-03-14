package com.walisport.module_setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.walisport.module_setting.databinding.ActivitySettingBinding

/**
 * 瓦力体育设置界面
 */

class SettingActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
    }

    private fun initView(){
        mBinding.relSettingOdds.setOnClickListener{

        }
        mBinding.relSettingNotice.setOnClickListener{

        }
        mBinding.relSettingBg.setOnClickListener{

        }
        mBinding.relSettingLanguage.setOnClickListener{

        }
    }
}