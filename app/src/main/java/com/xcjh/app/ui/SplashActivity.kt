package com.xcjh.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.engagelab.privates.push.api.MTPushPrivatesApi
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivitySplashBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.utils.toJson
import org.json.JSONObject
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
            Log.i("push===Splash_Receiver", "onIntent:${intent?.data}")
            Log.i("push===Splash_Receiver", "onIntent:${Gson().toJson(intent?.data)}")
            Log.i("push===Splash_Receiver", "onIntentgson:${intent?.extras.toJson()}")
            Log.i("push===Splash_Receiver", "onIntentgson:${Gson().toJson(intent?.extras)}")
            mDatabind.root.postDelayed({
                startNewActivity<MainActivity>{
                    intent?.extras?.let {
                        putExtras(it)
                    }
                }
                finish()
            },1000)

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }


}