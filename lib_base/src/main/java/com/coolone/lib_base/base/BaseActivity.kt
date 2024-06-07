package com.coolone.lib_base.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.coolone.lib_base.base.inner.IBaseView
import com.coolone.lib_base.base.inner.INetView
import kotlinx.coroutines.launch

/**
 *
 */
abstract class BaseActivity<V : ViewBinding, VM : ViewModel> : AppCompatActivity(), IBaseView,
    INetView {
    private lateinit var mSplashScreen: SplashScreen
    protected lateinit var mBinding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSplashScreen = installSplashScreen()
        initView(savedInstanceState)
        initData()
        lifecycleScope.launch {

        }
    }


}