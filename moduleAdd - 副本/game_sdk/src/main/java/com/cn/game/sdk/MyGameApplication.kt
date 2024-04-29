package com.cn.game.sdk

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.cn.game.sdk.event.AppGameViewModel
import com.xcjh.base_lib.App 
import me.jessyan.autosize.AutoSizeConfig

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appGameViewModel: AppGameViewModel by lazy { MyGameApplication.appGameViewModelInstance }

open class MyGameApplication  : App() , LifecycleObserver {

    companion object {

        lateinit var appGameViewModelInstance: AppGameViewModel


    }

    override fun onCreate() {
        super.onCreate()
        AutoSizeConfig.getInstance().isExcludeFontScale = true
        appGameViewModelInstance = getAppViewModelProvider()[AppGameViewModel::class.java]
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)


    }






}