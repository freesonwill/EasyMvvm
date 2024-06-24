package com.cn.game.sdk2.websocket.interfaces

import android.view.View

interface IGameForApp {

    fun loadGame()

    fun loginGameWithAgentName(agentName: String, token: String, callback: SDKLoginCallbackListener)

    fun enterLive(liveId: String, gameIds: List<Int>, data: String,callback: SDKEnterLiveCallbackListener)

    fun leaveLive(liveId:String,callback: SDKLeaveLiveCallbackListener)

    fun cancelGame(callback: SDKCancelGameCallbackListener)

    fun gameFloatingDetailViewStatusWithBlock(isShow:Boolean)

    fun allowedBet(isAllow:Boolean)

    fun floatingView(): View?

    fun resultView():View?

}