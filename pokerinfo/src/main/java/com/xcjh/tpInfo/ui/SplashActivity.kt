package com.xcjh.tpInfo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.tpInfo.base.BaseActivity
import com.xcjh.tpInfo.databinding.ActivitySplashBinding
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