package com.cn.game.sdk

import androidx.lifecycle.LifecycleObserver
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.event.EventViewModel
import com.xcjh.base_lib.App

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewModel: AppViewModel by lazy { MyGameApplication.appViewModelInstance }
//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy {
    MyGameApplication.eventViewModelInstance
}

class MyGameApplication  : App() , LifecycleObserver {

    companion object {

        lateinit var appViewModelInstance: AppViewModel
        lateinit var eventViewModelInstance: EventViewModel

    }

    override fun onCreate() {
        super.onCreate()

    }

}