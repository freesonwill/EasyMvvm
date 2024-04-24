package com.cn.game.sdk.utils

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk.MyGameApplication.Companion.appGameViewModelInstance
import com.xcjh.app.event.AppGameViewModel
import com.xcjh.base_lib.App.Companion.app

/**
 * 初始化
 */
object GamePartyLibraryInitializer {
    fun initialize(context: ViewModelStoreOwner,mApp:Application){
        appGameViewModelInstance= ViewModelProvider(context)[AppGameViewModel::class.java]
        app= mApp
     }
}