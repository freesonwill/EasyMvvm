package com.cn.game.sdk2.utils

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.imp.GameSDK

/**
 * 初始化
 */
object GamePartyLibraryInitializer {
        var mAppContext:Application?=null


        fun initialize(context: ViewModelStoreOwner, mApp:Application){
               // appGameViewModelInstance= ViewModelProvider(context)[AppGameViewModel::class.java]
                mAppContext= mApp
                Thread {
                        GameSDK.loadGame()
                        //初始化step1:登录
                        GameSDK.loginGameWithAgentName(
                                "wali-internal",
                                "81:zdw2oSuv&credit=915380.91&name=Aubrey Wolff"
                        )
                }.start()
                //初始化获取到快三结果的View
//        MyGameManager.getOpenResultView(mAppContext!!)
//        //初始化快三的浮动view
//        MyGameManager.getFastThreeView(mAppContext!!)
                //启动通讯

        }


}