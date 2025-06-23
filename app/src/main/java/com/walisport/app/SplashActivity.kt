package com.walisport.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import com.walisport.app.ui.viewmodel.SplashViewModel
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.app.ui.MainActivity
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.ui.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.websocket.data.LoginTokenFailedError
import arch.cayenne.lib.websocket.data.ResponseTimeOutError
import kotlin.random.Random
import kotlin.reflect.KClass

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    //zhangsan
    //55468809
    // token=NTU0Njg4MDlfMTc1MDMyNDQ5Nzk4NDo3ZGlxQUhkUmtEa1VBcVBu

    //wangzai
    //55468810
    // token=NTU0Njg4MTBfMTc1MDIzOTUzOTU0ODp4R3UzYUM1elJ0WDd4NXE0

    //xiaoyang
    //55468807
    // token=NTU0Njg4MDdfMTc1MDMyNDYyNTE1NDptNVBVWmtJN0ZqbkdFWTZo

    //wenxi
    //55468811
    // token=NTU0Njg4MTFfMTc0NzEyOTcxNjYxMTpBZGp3WU1YVFd2OTBTUG83

    //aquan
    //55468812
    // token=NTU0Njg4MTJfMTc1MDMyNDgxNzU4MzozUzF4VkV4ZlA3ZFF1QUNl

    //kc
    //uid=55468808
    //token=NTU0Njg4MDhfMTc1MDMyNDg1MzIwNjpkdWs0ejY3Q2hZQWdZRUR3

    //link
    //55468813
    // token=NTU0Njg4MTNfMTc1MDMyNDkzNDA5NjpJR3hCVDV4M1lXVHJ1Z3ZF

    //jeremy
    //55468814
    // token=NTU0Njg4MTRfMTc1MDMyNDk2NzcxODozMU1ISnlqcWlta1E1OTZF

    //joseph
    //55468815
    // token=NTU0Njg4MTVfMTc1MDMyNDk5NjQ2NTpldFI0RVBaWFhKRjN1cktj

    //ricky
    //55468816
    // token=NTU0Njg4MTZfMTc1MDMyNTAyMzI1ODpwSHFHUHBvZzhmM3R4UWNu

    //qatest1
    //55468822
    //token=NTU0Njg4MjJfMTc1MDMyNTIxMjY3NDpNSnlXYUFnNk9mc1I2cE9U

    //qatest2
    //55468823
    //token=NTU0Njg4MjNfMTc1MDMyNTIzOTAyNzoxdXZOSmtqRVZRemlDT2s0

    //qatest3
    //55468824
    //token=NTU0Njg4MjRfMTc1MDMyNTI2NjI1Mjo3ZDVUejljSXNFa05sWjI0

    //qatest4
    //55468825
    //token=NTU0Njg4MjVfMTc1MDMyNTI5MjY3NTpQY0NFdk1WQ1JpTGlJZFYy

    //qatest5
    //55468826
    //token=NTU0Njg4MjZfMTc1MDMyNTMyMzAxNDpyb1RVNlNJTFN3N05JTmxP

    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair<Int, String>(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        listOf(
            Pair(55468822, "NTU0Njg4MjJfMTc1MDMyNTIxMjY3NDpNSnlXYUFnNk9mc1I2cE9U"),
            Pair(55468823, "NTU0Njg4MjNfMTc1MDMyNTIzOTAyNzoxdXZOSmtqRVZRemlDT2s0"),
            Pair(55468824, "NTU0Njg4MjRfMTc1MDMyNTI2NjI1Mjo3ZDVUejljSXNFa05sWjI0"),
            Pair(55468825, "NTU0Njg4MjVfMTc1MDMyNTI5MjY3NTpQY0NFdk1WQ1JpTGlJZFYy"),
            Pair(55468826, "NTU0Njg4MjZfMTc1MDMyNTMyMzAxNDpyb1RVNlNJTFN3N05JTmxP")
        ).let { it[Random.nextInt(it.size)] }
    } else {
        Pair(0, "")
    }

    private val uid = pair.first
    private val token = pair.second

    override val vbClass: KClass<ActivitySplashBinding> = ActivitySplashBinding::class
    override val vmClass: KClass<SplashViewModel> = SplashViewModel::class

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarDarkFont = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(
            autoPadding = true,
            noPaddingViewIds = listOf(mBinding.splashBg.id)
        )
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
                mViewModel.ignore.value =  true
            }
        }
    }

    override fun createObserver() {
        mViewModel.homeTimeSeconds.observe(this) { seconds ->
            mBinding.splashCounterDown.text = getString(R.string.splash_counter_down_skip, seconds.toString())
        }
        launch(Lifecycle.State.RESUMED) {
            mViewModel.jumpToMainOrLogin.collect {
                if (!it) {
                    //TODO 跳到登入頁
                } else {
                    jumpToMainActivity()
                }
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