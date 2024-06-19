package com.cn.game.sdk2.websocket.imp

import android.view.View
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameMassageManager
import com.cn.game.sdk2.websocket.interfaces.IAppForGame
import com.cn.game.sdk2.websocket.interfaces.IGameForApp
import com.cn.game.sdk2.websocket.isAllowedBet
import com.cn.game.sdk2.websocket.isShowGame
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
     */
    override fun loginGameWithAgentName(agentName: String, token: String) {
        val req =
            ClientReq.LoginReq.newBuilder().setAgentName(agentName).setServer(8).setToken(token)
                .setRequestId(6).setVersion("6").setNickname("android").build()
        gameMassageManager?.login(req)
        gameMassageManager?.enterInfo()
    }

    /** 进入直播間
     * - Parameter liveId: 直播間id
     * - Parameter gIds: 遊戲ids
     * - Parameter data_p: 透传资料（转抛）
     * - type ==1 成功 随便
     */
    override fun enterLive(liveId: String, gameIds: List<Int>, data: String) {
        val req = GameReq.EnterGroup.newBuilder()
        var index = 0
        var index1 = 0
        var index2 = 0
        gameIds.forEach {
            req.setMiniGameIds(index, it)
            index++
        }
        val build = req.setData(data).setId(liveId).build()

        gameMassageManager?.enterGroup(build)
        gameMassageManager?.enterGame(GameReq.EnterMiniGame.newBuilder().setMiniGameId(gameIds[0]).build())
    }

    /** 离开直播間
     * - Parameter liveId: 直播間id
     */
    override fun leaveLive(liveId: String) {
        gameMassageManager?.levelGroup()
    }

    /**
     * 注销游戏
     */
    override fun cancelGame() {
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