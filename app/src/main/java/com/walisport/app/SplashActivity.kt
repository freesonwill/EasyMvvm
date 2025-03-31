package com.walisport.app

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.walisport.app.data.SplashViewModel
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.lib_base.ui.BaseActivity
import com.walisport.lib_base.ui.viewBind
import com.walisport.lib_base.utils.LogUtilsExt.logd
import com.walisport.module_setting.SettingActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    //zhangsan
    //55468809
    // token=NTU0Njg4MDlfMTc0Mjk2MDQ1ODA3MjpETVdiTUZXRVRkbWF3NE9j

    //wangzai
    //55468810
    // token=NTU0Njg4MTBfMTc0Mjk2MDM3MzAzODpVeEcwNmw4ZmxsV01lUmRF

    //xiaoyang
    //55468807
    // token=NTU0Njg4MDdfMTc0Mjk2MDYzODE0ODpES1FFSkxKYWEydXNQY3pN

    //wenxi
    //55468811
    // token=NTU0Njg4MTFfMTc0Mjk2MDUxMTA5ODplemdFTWdaN0FTZk0zNHRn

    //aquan
    //55468812
    // token=NTU0Njg4MTJfMTc0Mjk2MDU5Mzk3OTpzREp6dG1HeEY0UTlXYXZz

    //kc
    //uid=55468808
    //token=NTU0Njg4MDhfMTc0Mjk1OTUwNTkzMjpyS3gyZnNBYUp1cUdKcFRR

    //link
    //55468813
    // token=NTU0Njg4MTNfMTc0Mjk2MDk0MDU1ODpqSVZLbzkyOEtyTGZNdE05

    //jeremy
    //55468814
    // token=NTU0Njg4MTRfMTc0Mjk2MTE4NzgxMTpINlNOa2Vja0NRVUozeExj

    //joseph
    //55468815
    // token=NTU0Njg4MTVfMTc0Mjk2MTI1ODE5MTpQZk9kb0NGTVUwWHlZaXEx

    //ricky
    //55468816
    // token=NTU0Njg4MTZfMTc0Mjk2MTQ1NzMzNjp3UGxVY1BJM0wxancwZGZm

    //android1
    //55468817
    // token=NTU0Njg4MTdfMTc0Mjk2MTUxNzMxMzptSWpQekR3cjdRTUsyT1F2

    //android2
    //55468818
    // token=NTU0Njg4MThfMTc0Mjk2MTU2Njc3MTpLejA1ZnpWUDFidkR1Mkd5

    //android3
    //55468819
    // token=NTU0Njg4MTlfMTc0Mjk2MTY1MTM5MDpiZmFFRmhCOVZBTUVWcjUz

    //android4
    //55468820
    // token=NTU0Njg4MjBfMTc0Mjk2MTY4NjkyMzpVZUVBdHh6d01GbngyZXBl

    //android5
    //55468821
    // token=NTU0Njg4MjFfMTc0Mjk2MjA4NDE4OTpHZ1djRDh4OGJPQ2FuV2dl

    //qatest1
    //55468822
    // token=NTU0Njg4MjJfMTc0Mjk2MjExMzc1MjpNclRlTHhQYjVyakRxOXhO

    //qatest2
    //55468823
    // token=NTU0Njg4MjNfMTc0Mjk2MjE0NjUxMDoweE96MGhVY242MEFrQ3c1

    //qatest3
    //55468824
    // token=NTU0Njg4MjRfMTc0Mjk2MjE3ODc4MTo2d1Zqa3lPSmlmSXdZVGla

    //qatest4
    //55468825
    // token=NTU0Njg4MjVfMTc0Mjk2MjIxOTgzOTp4Q2VGTXVzaWkweGZFSEZN

    //qatest5
    //55468826
    // token=NTU0Njg4MjZfMTc0Mjk2MjI0NzAxODpsUmdEU1o1MlkyZnRBQlV1

    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair<Int, String>(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        listOf(
            Pair(55468822, "NTU0Njg4MjJfMTc0Mjk2MjExMzc1MjpNclRlTHhQYjVyakRxOXhO"),
            Pair(55468823, "NTU0Njg4MjNfMTc0Mjk2MjE0NjUxMDoweE96MGhVY242MEFrQ3c1"),
            Pair(55468824, "NTU0Njg4MjRfMTc0Mjk2MjE3ODc4MTo2d1Zqa3lPSmlmSXdZVGla"),
            Pair(55468825, "NTU0Njg4MjVfMTc0Mjk2MjIxOTgzOTp4Q2VGTXVzaWkweGZFSEZN"),
            Pair(55468826, "NTU0Njg4MjZfMTc0Mjk2MjI0NzAxODpsUmdEU1o1MlkyZnRBQlV1")
        ).let { it[Random.nextInt(it.size)] }
    } else {
        Pair(0, "")
    }

    private val uid = pair.first
    private val token = pair.second

    override val mBinding: ActivitySplashBinding by viewBind()
    override val mViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "uid:$uid, token:$token".logd(TAG)
        mViewModel.startSocketConnectAndLogin(uid, token)
    }


    override fun initView(savedInstanceState: Bundle?) {

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

    private fun jumpToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}