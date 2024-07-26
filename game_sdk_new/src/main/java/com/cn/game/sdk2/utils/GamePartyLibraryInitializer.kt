package com.cn.game.sdk2.utils

import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk2.websocket.imp.GameApp
import com.xcjh.base_lib2.utils.loge

/**
 * 初始化
 */
object GamePartyLibraryInitializer {
    var mAppContext: Application? = null


    fun initialize(storeOwner: ViewModelStoreOwner, mApp: Application) {
        // appGameViewModelInstance= ViewModelProvider(context)[AppGameViewModel::class.java]
        mAppContext = mApp
        "初始化step1:loadGame".loge("GamePartyLibraryInitializer")
//        GameApp.loadGame(applicationContext,true, url,object :GameApp.OnSdkListener{
//            override fun onLoginGame(type: Int, msg: String?) {
//            }
//
//            override fun onEnterLive(type: Int, msg: String) {
//            }
//
//            override fun onEnterGame() {
//            }
//
//            override fun customerServiceAction() {
//            }
//
//            override fun historyOfBetAction() {
//            }
//
//            override fun onLeaveLive(type: Int, msg: String?) {
//            }
//
//            override fun onTokenLoseEffectiveness() {
//            }
//
//            override fun onGameFloatingDetailViewStatus(isShowUp: Boolean) {
//            }
//
//            override fun onInsufficientBalance() {
//            }
//        })

        //初始化获取到快三结果的View
//        MyGameManager.getOpenResultView(mAppContext!!)
//        //初始化快三的浮动view
//        MyGameManager.getFastThreeView(mAppContext!!)
        //启动通讯

    }

    override fun toString(): String {
        return "GamePartyLibraryInitializer(mAppContext=$mAppContext)"
    }


}