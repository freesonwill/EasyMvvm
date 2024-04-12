package com.xcjh.app.ui.home.my.operate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivitySplashBinding
import com.xcjh.app.databinding.ActivityVibrateSetBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.base.BaseViewModel

/**
 * 震动设置
 */
class VibrateSetActivity: BaseActivity<BaseViewModel, ActivityVibrateSetBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        mDatabind.titleTop.tvTitle.text=resources.getString(R.string.vibrate_txt_title)
        //收到通知其他地方登录
        appViewModel.quitLoginEvent.observe(this){
            finish()
        }
        mDatabind.sbVibrateAnchor.isChecked=CacheUtil.isAnchorVibrate()
        mDatabind.sbVibrateNavbar.isChecked=CacheUtil.isNavigationVibrate()
        
        mDatabind.sbVibrateAnchor.setOnClickListener {
            CacheUtil.setAnchorVibrate(mDatabind.sbVibrateAnchor.isChecked)

        }

        mDatabind.sbVibrateNavbar.setOnClickListener {
            CacheUtil.setNavigationVibrate(mDatabind.sbVibrateNavbar.isChecked)

        }
    }
}