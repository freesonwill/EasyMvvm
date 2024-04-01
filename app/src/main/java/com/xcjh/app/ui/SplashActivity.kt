package com.xcjh.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.gyf.immersionbar.ImmersionBar
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.xcjh.app.R
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivitySplashBinding
import com.xcjh.base_lib.Constants
import java.util.*


/**
 * 传统启动页面
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<BaseViewModel, ActivitySplashBinding>() {
    private var secondsRemaining: Long = 0L
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()


        onIntent(intent)
        //createTimer(2)


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onIntent(intent)
    }

    private fun onIntent(intent: Intent?) {
        try {
            Log.i("push===Splash_Receiver", "onIntent-extras:${intent?.extras.toString()}")
            Log.i("push===Splash_Receiver", "onIntent-data:${intent?.data}")
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//                mDatabind.root.postDelayed({
//                    startNewActivity<MainActivity> {
//                        intent?.extras?.let {
//                            putExtras(it)
//                        }
//                    }
//                    finish()
//                }, 1000)
//            } else {
//                startNewActivity<MainActivity> {
//                    intent?.extras?.let {
//                        putExtras(it)
//                    }
//                }
//                finish()
//            }
            startNewActivity<MainActivity> {
                intent?.extras?.let {
                    putExtras(it)
                }
            }
            finish()

        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

//    override fun createObserver() {
//        super.createObserver()
//        mViewModel.liveList.observe(this) {
//            if (mViewModel.dateSetOf != null && (mViewModel.dateSetOf.advertisement.size > 0 ||
//                        mViewModel.dateSetOf.match.size > 0 || !it.isFirstEmpty)) {
//                var  data= MainDataBean()
//                data.advertisement=mViewModel.dateSetOf.advertisement
//                data.match=mViewModel.dateSetOf.match
//                data.list.addAll(it.listData)
//                CacheUtil.setMainFirst(Gson().toJson(data))
//                onIntent(intent)
//                finish()
//
//            } else {
//                lifecycleScope.launch {
////                            delay(10000) // 延迟 10 秒
//                    delay(2000) // 延迟 10 秒
//                    mViewModel.getBannerList()
//                }
//            }
//        }
//    }
}