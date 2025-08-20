package com.walisport.app

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.app.ui.MainActivity
import com.walisport.app.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    //token=NTU0Njg4MjJfMTc1MTM1NTAxMDI3MDppUjNheWVyczZ4S3dyVEFX

    //qatest2
    //55468823
    //token=NTU0Njg4MjNfMTc1MTM1NDk3MzM3MTpsMEZTMTNoUzNONTJDRTIz

    //qatest3
    //55468824
    //token=NTU0Njg4MjRfMTc1MTM1NDkzNzg0NTpUZ3BsZGF6dXhHSGJYQUZo

    //qatest4
    //55468825
    //token=NTU0Njg4MjVfMTc1MTM1NDg2NjEyMDoyZzh0Tk52eXF4VGxMZlA5

    //qatest5
    //55468826
    //token=NTU0Njg4MjZfMTc1MTM1NDgxMzUwMjoyNVJHMnVObXVhNnk5NnpC

    //qatest
    //55469215
    //NTU0NjkyMTVfMTc1NTI2MjU1NjI0NTpveXFjVzFTc3h5TTJwY1Z6

    //tiger_test
    //55469214
    //NTU0NjkyMTRfMTc1NTI1Mjg5NDMzMTpiNlhJeVphb0xOQVFnNWpS

    //yuze  test
    //55469174
    //NTU0NjkxNzRfMTc1MzYwNzAxOTAwMjpYWUQ0Tm12Vjc1YTlDTnFi

    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair<Int, String>(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        listOf(
            Pair(55468822, "NTU0Njg4MjJfMTc1NTE2NzgxMzA0MDpkaGFZSUFsR1MzTlljQ3lZ"),
            Pair(55468823, "NTU0Njg4MjNfMTc1NDk3MjgwNDY3NzpiNjRyMDJ2Y3RUeXc5SVd5"),
            Pair(55468824, "NTU0Njg4MjRfMTc1NDk3Mjg2MDg2OTo2UUU1bmZ3VlpjUjh4ZTk5"),
            Pair(55468825, "NTU0Njg4MjVfMTc1NDk3MjkyODAyMTphVjdUOEcxSG1OTDR6Sjlw"),
            Pair(55468826, "NTU0Njg4MjZfMTc1NDk3Mjk1OTg1NjpEWXp6U0JDNlRIdThjSUcy")
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
        lifecycleScope.launch {
            delay(100)
            jumpToMainActivity()
        }

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun jumpToMainActivity() {
        navigate(Intent(this, MainActivity::class.java))
        finish()
    }

}