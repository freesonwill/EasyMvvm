package com.walisport.app

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.walisport.app.data.SplashViewModel
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    override val mBinding: ActivitySplashBinding by viewBind()
    override val mViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        titleBgColor = android.R.color.transparent
    }

    override fun initListener() {
        mBinding.apply {
            splashCounterDown.setOnClickListener {
                jumpToMainActivity()
            }
        }
    }

    override fun createObserver() {
        mViewModel.homeTimeSeconds.observe(this) { seconds ->

            if (seconds == 0) {
                jumpToMainActivity()
            } else {
                mBinding.splashCounterDown.text =
                    getString(R.string.splash_counter_down_skip, seconds.toString())
            }
        }
    }

    override fun lazyLoadData() {
    }

    private fun jumpToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}