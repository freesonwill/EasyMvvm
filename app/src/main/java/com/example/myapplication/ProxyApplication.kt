package com.example.myapplication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cn.game.sdk2.websocket.imp.GameApp


class ProxyApplication : Application() {
    companion object {
        lateinit var instance :ProxyApplication
    }
    private val url:String get()  = when(BuildConfig.BUILD_TYPE) {
        "release"-> "ws://35.220.148.132:7642"  //连调
        else-> "wss://ws.qxe68.com:7001/api/game/5702" ///test
    }
    private val  _onGameAppEvent = MutableLiveData<Pair<String,Map<String,Any?>>>()
    private val _onSocketConnected :MutableLiveData<Boolean> = MutableLiveData()
    val onSocketConnected :LiveData<Boolean> = _onSocketConnected
    val onGameSdkEvent :LiveData<Pair<String,Map<String,Any?>>> = _onGameAppEvent



    override fun onCreate() {
        super.onCreate()
        instance = this
        loadGame()
    }

    fun loadGame(){
        GameApp.loadGame(applicationContext,true,  url, "", object :GameApp.OnSdkListener {
            override fun onSocketConnected() {
                _onSocketConnected.value = true
            }

            override fun onSocketClosed() {
                _onSocketConnected.value = false
            }
            override fun onClickOtherGameWithBlock(json: String) {
                _onGameAppEvent.value = Pair("onClickOtherGameWithBlock", mapOf("json" to json))
            }

            override fun onCustomerServiceAction() {
                _onGameAppEvent.value = Pair("onCustomerServiceAction", mapOf())
            }

            override fun onEnterGame() {
                _onGameAppEvent.value = Pair("onEnterGame", mapOf())
            }

            override fun onEnterLive(type: Int, msg: String) {
                _onGameAppEvent.value = Pair("onEnterLive", mapOf("type" to type,"msg" to msg))
            }

            override fun onGameFloatingDetailViewStatus(isShowUp: Boolean) {
                _onGameAppEvent.value = Pair("onGameFloatingDetailViewStatus", mapOf("isShowUp" to isShowUp))
            }

            override fun onHistoryOfBetAction() {
                _onGameAppEvent.value = Pair("onHistoryOfBetAction", mapOf())
            }

            override fun onInsufficientBalance() {
                _onGameAppEvent.value = Pair("onInsufficientBalance", mapOf())
            }

            override fun onLeaveLive(liveId: String, type: Int, msg: String?) {
                _onGameAppEvent.value = Pair("onLeaveLive", mapOf("liveId" to liveId,"type" to type,"msg" to msg))
            }

            override fun onLoginGame(type: Int, msg: String?) {
                _onGameAppEvent.value = Pair("onLoginGame", mapOf("type" to type,"msg" to msg))
            }

            override fun onTokenLoseEffectiveness() {
                _onGameAppEvent.value = Pair("onTokenLoseEffectiveness", mapOf())
            }
        })
    }
}