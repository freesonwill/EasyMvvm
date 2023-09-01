package com.coolone.mvvm

import androidx.lifecycle.ViewModelProvider
import com.coolone.lib_base.BaseActivity
import com.coolone.mvvm.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, EmptyViewModel>() {

    override fun initViewModel(): EmptyViewModel {
        return ViewModelProvider(this)[EmptyViewModel::class.java]
    }

    override fun layoutResId(): Int {
        return R.layout.activity_main
    }
}