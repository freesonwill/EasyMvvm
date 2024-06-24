package com.cn.game.sdk2.websocket.imp

import android.view.View
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameMassageManager
import com.cn.game.sdk2.websocket.interfaces.IAppForGame
import com.cn.game.sdk2.websocket.interfaces.IGameForApp
import com.cn.game.sdk2.websocket.interfaces.SDKCallbackListener
import com.cn.game.sdk2.websocket.isAllowedBet
import com.cn.game.sdk2.websocket.isShowGame
import com.cn.game.sdk2.websocket.mCallback
import game.common.proto.ClientReq
import game.mod.proc.yf.proto.req.GameReq

/**
 * 提供给app调用的方法
 */
object GameSDK : IGameForApp {

    /**
     * app需要实现IAppForGame接口
     * 用于sdk调用
     *  - 历史记录
     *  - 联系客服
     *  - token失效
     */
    fun setOnMessageForAppListener(listener: IAppForGame) {
        appListener = listener
    }

    /**
     * 加載SDK
     * app集成sdk 先调用此方法初始化websocket
     */
    override fun loadGame() {
        GameSocketManager.getInstance()?.initSocketClient()
    }

    /** 登录
     * - Parameter agentName: 平台名称
     * - Parameter token: 用户token
     * - type ==1 成功 type =1000（desc：您当前还在其他游戏中）type =1001 （desc：token验证失败）type =1002 （desc：余额不足）type =1005（desc：当前服务器正在维护）type =200（desc：其他情况）
     * - 初始化流程：1）App调用登录：loadGame + loginGameWithAgentName ——>2）进入房间坐下：GameServiceImp.enterInfo()
     * - ——>3)App进入直播间:GameServiceImp.groupInfo() ——>4)进入小游戏:GameServiceImp.gameInfo()
     */
    //platform= 6 ,requestId = 0,version = "1"
    override fun loginGameWithAgentName(
        agentName: String, token: String, callback: SDKCallbackListener
    ) {
        mCallback = callback
        val req = ClientReq.LoginReq.newBuilder().setPlatform(0).setRequestId(0).setVersion("1")
            .setNickname("Aubrey Wolff").setAgentName(agentName).setToken(token).build()
        gameMassageManager?.login(req)
    }

    /** 进入直播間
     * - Parameter liveId: 直播間id
     * - Parameter gIds: 遊戲ids
     * - Parameter data_p: 透传资料（转抛）
     * - type ==1 成功 随便
     */
    //1213,3
    override fun enterLive(
        liveId: String, gameIds: List<Int>, data: String, callback: SDKCallbackListener
    ) {
        mCallback = callback
        val req = GameReq.EnterGroup.newBuilder()
        gameIds.forEach {
            req.addMiniGameIds(it)
        }
        val build = req.setData(data).setId(liveId).build()

        gameMassageManager?.enterGroup(build)
        gameMassageManager?.enterGame(
            GameReq.EnterMiniGame.newBuilder().setMiniGameId(gameIds[0]).build()
        )
    }

    /** 离开直播間
     * - Parameter liveId: 直播間id
     */
    override fun leaveLive(liveId: String, callback: SDKCallbackListener) {
        mCallback = callback
        gameMassageManager?.levelGroup()
    }

    /**
     * 注销游戏
     */
    override fun cancelGame(callback: SDKCallbackListener) {
        mCallback = callback
        GameSocketManager.getInstance()?.stopService()
    }

    /**
     * 是否彈出遊戲框
     */
    override fun gameFloatingDetailViewStatusWithBlock(isShow: Boolean) {
        isShowGame = isShow
    }

    /**
     * 是否允許下注
     * - Parameter isAllow: 默認true
     */
    override fun allowedBet(isAllow: Boolean) {
        isAllowedBet = isAllow
    }

    /**
     * 入口漂浮窗視圖
     */
    override fun floatingView(): View? {
        return com.cn.game.sdk2.websocket.floatingView
    }

    /**
     * 结果视图
     */
    override fun resultView(): View? {
        return com.cn.game.sdk2.websocket.resultView
    }


}