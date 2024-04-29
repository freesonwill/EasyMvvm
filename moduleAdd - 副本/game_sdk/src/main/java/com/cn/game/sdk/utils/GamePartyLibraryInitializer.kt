package com.cn.game.sdk.utils

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk.MyGameApplication.Companion.appGameViewModelInstance
import com.cn.game.sdk.event.AppGameViewModel
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.AppContext

/**
 * 初始化
 */
object GamePartyLibraryInitializer {
        var mAppContext:Application?=null

    fun initialize(context: ViewModelStoreOwner,mApp:Application){
        appGameViewModelInstance= ViewModelProvider(context)[AppGameViewModel::class.java]
        mAppContext= mApp
     }


//    fun setApp(mApp:Application){
//        appGame= mApp
//    }


    fun setLog(){
        Log.i("SSSSSSSSSSSSSSs","===============")
    }
}