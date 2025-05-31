package com.walisport.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import com.walisport.app.ui.viewmodel.SplashViewModel
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.app.ui.MainActivity
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.websocket.data.LoginTokenFailedError
import arch.cayenne.lib.websocket.data.ResponseTimeOutError
import kotlin.random.Random
import kotlin.reflect.KClass

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    //zhangsan
    //55468809
    // token=NTU0Njg4MDlfMTc0NzEyOTYzODM2Nzp1NzhzbW1ybHBiQlJqcUhJ

    //wangzai
    //55468810
    // token=NTU0Njg4MTBfMTc0NzEyOTY4NDEyMTpwMHVDUHhoRnpzRnNkbXdx

    //xiaoyang
    //55468807
    // token=NTU0Njg4MDdfMTc0NzEyOTQyODg1NDpGRmp2SUhhN2hsWThtMXZS

    //wenxi
    //55468811
    // token=NTU0Njg4MTFfMTc0NzEyOTcxNjYxMTpBZGp3WU1YVFd2OTBTUG83

    //aquan
    //55468812
    // token=NTU0Njg4MTJfMTc0NzEyOTc1ODE1ODpqd1BDVURxcTRkQWhzeWFy

    //kc
    //uid=55468808
    //token=NTU0Njg4MDhfMTc0NzEyOTc4ODA4MTpQYUNMcXFFbVVWVFBKak9M

    //link
    //55468813
    // token=NTU0Njg4MTNfMTc0NzEyOTgyODE0OTp5blR0RXhvcFJTdEFrbURq

    //jeremy
    //55468814
    // token=NTU0Njg4MTRfMTc0NzEyOTg2NDMyNjpFb004VEc1Y3FzeFlmWU12

    //joseph
    //55468815
    // token=NTU0Njg4MTVfMTc0NzEyOTg5MDc2NDoxQ3BMUGQzRkl4RG5qTUVT

    //ricky
    //55468816
    // token=NTU0Njg4MTZfMTc0NzEyOTkzNTk1MTp0dm5oQ3lwcXhNeTR5Ykdu

    //qatest1
    //55468822
    //token=NTU0Njg4MjJfMTc0ODU5NjMyOTk4MDoyM2NxdG9idmxIdUlrNUdV

    //qatest2
    //55468823
    //token=NTU0Njg4MjNfMTc0ODU5NjM3MDE5MjpvbU9OSjNZSUMzRnJzeHdM

    //qatest3
    //55468824
    //token=NTU0Njg4MjRfMTc0ODU5NjQxNTIyNjp3STNyZFdxMUtod0gwVTda

    //qatest4
    //55468825
    //token=NTU0Njg4MjVfMTc0ODU5NjQ3NTk0NjplME9rQm1KYzZxb3d3WXls

    //qatest5
    //55468826
    //token=NTU0Njg4MjZfMTc0ODU5NjUxNTE1MDo3R1FEdWdPNjFjMmNmQzZX

    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair<Int, String>(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        listOf(
            Pair(55468822, "NTU0Njg4MjJfMTc0ODU5NjMyOTk4MDoyM2NxdG9idmxIdUlrNUdV"),
            Pair(55468823, "NTU0Njg4MjNfMTc0ODU5NjM3MDE5MjpvbU9OSjNZSUMzRnJzeHdM"),
            Pair(55468824, "NTU0Njg4MjRfMTc0ODU5NjQxNTIyNjp3STNyZFdxMUtod0gwVTda"),
            Pair(55468825, "NTU0Njg4MjVfMTc0ODU5NjQ3NTk0NjplME9rQm1KYzZxb3d3WXls"),
            Pair(55468826, "NTU0Njg4MjZfMTc0ODU5NjUxNTE1MDo3R1FEdWdPNjFjMmNmQzZX")
        ).let { it[Random.nextInt(it.size)] }
    } else {
        Pair(0, "")
    }

    private val uid = pair.first
    private val token = pair.second

    override val vbClass: KClass<ActivitySplashBinding> = ActivitySplashBinding::class
    override val vmClass: KClass<SplashViewModel> = SplashViewModel::class

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        return StatusBarConfig
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.loadMyAppSkin()
    }

    override fun initData() {
        super.initData()
        "uid:$uid, token:$token".logd(TAG)
        mViewModel.saveUserData(uid, token)  //TODO 實作登入頁後就不需要這個了
        mViewModel.connectToServer()
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
            mBinding.splashCounterDown.text = getString(R.string.splash_counter_down_skip, seconds.toString())
        }

        mViewModel.jumpToMainOrLogin.observe(this) {
            if (!it) {
                //TODO 跳到登入頁
            } else {
                jumpToMainActivity()
            }
        }

        mViewModel.connectingError.observe(this) {
            when(it) {
                is LoginTokenFailedError -> {   //準備登入時沒有取得token或是uid
                    //TODO 跳到登入頁
                }
                is ResponseTimeOutError -> {    //send login timeout
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
                else -> {   //其餘錯誤
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun jumpToMainActivity() {
        navigate(Intent(this, MainActivity::class.java))
        finish()
    }

}