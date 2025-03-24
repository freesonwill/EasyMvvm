package com.walisport.app

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.walisport.app.data.MainViewModel
import com.walisport.app.databinding.ActivityMainBinding
import com.walisport.lib_base.data.remote.ApiResponseState
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val mBinding: ActivityMainBinding by viewBind()
    override val mViewModel: MainViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.startSocketConnect()
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.onApiResponseStateListener.observe(this) { api ->
            // hide loaind
            when (api) {
                ApiResponseState.Processing -> {
                    // show loading
                }
                ApiResponseState.Failed() -> {
                    // show toast
                    val msg = (api as ApiResponseState.Failed).desc
                }
                else -> {

                }
            }
        }
    }
}