package com.cn.game.sdk2.websocket.interfaces

import android.view.View

interface IGameForApp {

    fun loadGame()

    fun loginGameWithAgentName(agentName: String, token: String, callback: SDKCallbackListener)

    fun enterLive(liveId: String, gameIds: List<Int>, data: String,callback: SDKCallbackListener)

    fun leaveLive(liveId:String,callback: SDKCallbackListener)

    fun cancelGame(callback: SDKCallbackListener)

    fun gameFloatingDetailViewStatusWithBlock(isShow:Boolean)

    fun allowedBet(isAllow:Boolean)

    fun floatingView(): View?

    fun resultView():View?

}