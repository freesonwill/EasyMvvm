package com.walisport.app

import android.content.Intent
import android.os.Bundle
import com.walisport.app.data.SplashViewModel
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    override val mBinding: ActivitySplashBinding by viewBind()
    override val mViewModel: SplashViewModel by viewModel()


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.apply {
            splashCounterDown.setOnClickListener {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    override fun lazyLoadData() {
    }
}