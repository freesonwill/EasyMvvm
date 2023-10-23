package com.xcjh.app.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivitySplashBinding
import java.util.*


/**
 * 传统启动页面
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {
    private var secondsRemaining: Long = 0L
    override fun initView(savedInstanceState: Bundle?) {

        super.initView(savedInstanceState)
        /*  ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .statusBarColor(android.R.color.transparent)
            .init()*/

        startMainActivity()
        //createTimer(2)

    }

    /** Start the MainActivity. */
    private fun startMainActivity() {
        startNewActivity<MainActivity>()
        finish()
    }

}