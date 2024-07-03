package com.cn.game.sdk2.utils

import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk2.websocket.imp.GameApp
import com.xcjh.base_lib.utils.loge

/**
 * 初始化
 */
object GamePartyLibraryInitializer {
    var mAppContext: Application? = null


    fun initialize(context: ViewModelStoreOwner, mApp: Application) {
        // appGameViewModelInstance= ViewModelProvider(context)[AppGameViewModel::class.java]
        mAppContext = mApp
        "初始化step1:loadGame".loge("GamePartyLibraryInitializer")
        GameApp.loadGame(mApp.applicationContext,true,object :GameApp.OnSdkListener{
            override fun customerServiceAction() {
            }

            override fun historyOfBetAction() {
            }

            override fun onEnterGame() {
            }

            override fun onEnterLive(type: Int, msg: String) {
            }

            override fun onLeaveLive(type: Int, str: String?) {
            }

            override fun onLoginGame(i: Int, str: String?) {
                GameApp.enterLive("1213", listOf(1), "")
            }

            override fun onTokenLoseEffectiveness() {
            }

            override fun onGameFloatingDetailViewStatus(isShowUp: Boolean) {
            }

            override fun onInsufficientBalance() {
            }

        })

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