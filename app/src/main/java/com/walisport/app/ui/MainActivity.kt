package com.walisport.app.ui

import android.os.Bundle
import com.walisport.app.data.MainViewModel
import com.walisport.app.databinding.ActivityMainBinding
import com.walisport.lib_base.data.remote.ApiResponseState
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import com.walisport.lib_base.utils.LogUtilsExt.logd
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val mBinding: ActivityMainBinding by viewBind()
    override val mViewModel: MainViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }

    companion object {
        val TAG = "MainActivity"
    }
}