package com.cn.game.sdk2.utils

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk2.websocket.GameSocketManager

/**
 * 初始化
 */
object GamePartyLibraryInitializer {
        var mAppContext:Application?=null


        fun initialize(context: ViewModelStoreOwner, mApp:Application){
               // appGameViewModelInstance= ViewModelProvider(context)[AppGameViewModel::class.java]
                mAppContext= mApp
                GameSocketManager.getInstance()?.initSocketClient()
                //初始化获取到快三结果的View
//        MyGameManager.getOpenResultView(mAppContext!!)
//        //初始化快三的浮动view
//        MyGameManager.getFastThreeView(mAppContext!!)
                //启动通讯

        }


}