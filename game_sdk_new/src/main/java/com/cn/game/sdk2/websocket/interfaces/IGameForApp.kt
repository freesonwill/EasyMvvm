package com.cn.game.sdk2.websocket.interfaces

import android.view.View

interface IGameForApp {

    fun loadGame()

    fun loginGameWithAgentName(agentName: String, token: String)

    fun enterLive(liveId: String, gameIds: List<Int>, data: String)

    fun leaveLive(liveId:String)

    fun cancelGame()

    fun gameFloatingDetailViewStatusWithBlock(isShow:Boolean)

    fun allowedBet(isAllow:Boolean)

    fun floatingView(): View?

    fun resultView():View?

}