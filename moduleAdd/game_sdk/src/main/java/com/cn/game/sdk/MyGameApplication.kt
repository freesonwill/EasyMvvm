package com.cn.game.sdk

import androidx.lifecycle.LifecycleObserver
import com.xcjh.app.event.AppGameViewModel
import com.xcjh.base_lib.App

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appGameViewModel: AppGameViewModel by lazy { MyGameApplication.appGameViewModelInstance }


class MyGameApplication  : App() , LifecycleObserver {

    companion object {

        lateinit var appGameViewModelInstance: AppGameViewModel


    }

    override fun onCreate() {
        super.onCreate()

    }

}