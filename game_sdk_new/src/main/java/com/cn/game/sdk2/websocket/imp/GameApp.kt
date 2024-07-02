package com.cn.game.sdk2.websocket.imp

import android.content.Context
import android.view.View
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appContext
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.gameMassageManager
import com.cn.game.sdk2.websocket.interfaces.GameApp
import com.cn.game.sdk2.websocket.interfaces.IGameForApp
import com.cn.game.sdk2.websocket.isNeedReconnect
import game.common.proto.ClientReq
import game.mod.proc.yf.proto.req.GameReq

/**
 * 提供给app调用的方法
 */
object GameApp : IGameForApp {

    /**
     * app需要实现IAppForGame接口
     * 用于sdk调用
     *  - 历史记录
     *  - 联系客服
     *  - token失效
     */

    /**
     * 加載SDK
     * app集成sdk 先调用此方法初始化websocket
     */
    override fun loadGame(
        context: Context,
        lifecycleEnable: Boolean,
        onSdkListener: GameApp.OnSdkListener
    ) {
        appContext = context
        appListener = onSdkListener
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
    override fun login(token: String, agentName: String, isAnchor: Boolean) {
        val req = ClientReq.LoginReq.newBuilder().setPlatform(6).setRequestId(0).setVersion("1")
            .setNickname("").setAgentName(agentName).setToken(token).build()
        gameMassageManager?.login(req)
    }

    /** 进入直播間
     * - Parameter liveId: 直播間id
     * - Parameter gIds: 遊戲ids
     * - Parameter data_p: 透传资料（转抛）
     * - type ==1 成功 随便
     */
    //1213,3
    override fun enterLive(liveId: String, gameIds: List<Int>, data: String) {
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
    override fun leaveLive() {
        gameMassageManager?.levelGroup()
    }

    /**
     * 注销游戏
     */
    override fun cancelGame() {
        isNeedReconnect = false
        GameSocketManager.getInstance()?.stopService()
    }

    /**
     * 是否彈出遊戲框
     */
    override fun gameFloatingDetailViewStatusWithBlock(isShow: Boolean) {
        gameAboutModel.isShowGame(isShow)
    }

    /**
     * 是否允許下注
     * - Parameter isAllow: 默認true
     */
    override fun allowedBet(isAllow: Boolean) {
        gameAboutModel.isAllowedBet(isAllowedBet = isAllow)
    }

    /**
     * 入口漂浮窗視圖
     */
    override fun createFloatEnterView(context:Context): View? {
        return com.cn.game.sdk2.websocket.floatingView
    }

    /**
     * 结果视图
     */
    override fun createFloatResultView(context:Context): View? {
        return com.cn.game.sdk2.websocket.resultView
    }

    override fun dismissFloatingController() {
        gameAboutModel.isShowGame(false)
    }

    override fun refreshScore() {
        gameMassageManager?.refreshScore()
    }

    fun onResume(){

    }

    fun onPause(){

    }

    fun onStop(){

    }

}