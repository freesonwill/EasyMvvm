package com.cn.game.sdk2.websocket.interfaces

import android.content.Context
import android.view.View

interface IGameForApp {

    fun loadGame(context: Context, lifecycleEnable: Boolean, onSdkListener: GameApp.OnSdkListener)

    fun login(token: String, agentName: String,isAnchor: Boolean)

    fun enterLive(liveId: String, gameIds: List<Int>, data: String)

    fun leaveLive()

    fun cancelGame()

    fun gameFloatingDetailViewStatusWithBlock(isShow: Boolean)

    fun allowedBet(isAllow: Boolean)

    fun createFloatEnterView(context:Context): View?

    fun createFloatResultView(context:Context): View?

    fun dismissFloatingController()

    fun refreshScore();

}