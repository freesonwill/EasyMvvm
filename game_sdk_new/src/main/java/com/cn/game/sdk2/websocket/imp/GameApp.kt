package com.cn.game.sdk2.websocket.imp

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appContext
import com.cn.game.sdk2.websocket.appLifecycleEnable
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.gameMassageManager
import com.cn.game.sdk2.websocket.interfaces.IGameForApp
import com.cn.game.sdk2.websocket.isEnableSound
import com.cn.game.sdk2.websocket.isNeedReconnect
import com.cn.game.sdk2.websocket.socketStatesCallback
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
        onSdkListener: OnSdkListener
    ) {
        appContext = context
        appLifecycleEnable = lifecycleEnable
        appListener = onSdkListener
        val initSocketClient = GameSocketManager.getInstance()?.initSocketClient()
        (appContext as Application).registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }

        })
    }

    /** 登录
     * - Parameter agentName: 平台名称
     * - Parameter token: 用户token
     * - type ==1 成功 type =1000（desc：您当前还在其他游戏中）type =1001 （desc：token验证失败）type =1002 （desc：余额不足）type =1005（desc：当前服务器正在维护）type =200（desc：其他情况）
     * - 初始化流程：1）App调用登录：loadGame + loginGameWithAgentName ——>2）进入房间坐下：GameServiceImp.enterInfo()
     * - ——>3)App进入直播间:GameServiceImp.groupInfo() ——>4)进入小游戏:GameServiceImp.gameInfo()
     */
    //platform= 6 ,requestId = 0,version = "1"
    override fun login(token: String, agentName: String, anchor: Boolean) {
        gameAboutModel.isAnchor = anchor
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
    @Deprecated("")
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

    override fun isShowHistoryAndCustomer(show: Boolean) {
        gameAboutModel.isShowHistoryAndCustomer = show
    }

    /**
     * 入口漂浮窗視圖
     */
    override fun createFloatEnterView(context:Context): View {
        if(appLifecycleEnable && context is Activity){
            (context as LifecycleOwner).lifecycle.addObserver(object :LifecycleEventObserver{
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when(event){
                        Lifecycle.Event.ON_RESUME->{
                            isEnableSound = true
                        }
                        Lifecycle.Event.ON_PAUSE->{
                            isEnableSound = false
                        }
                        Lifecycle.Event.ON_STOP->{
                            isEnableSound = false
                        }
                        else->{

                        }
                    }
                }

            })
        }
        return ViewHelper.getFastView(context)
    }

    /**
     * 结果视图
     */
    override fun createFloatResultView(context:Context): View {
        return ViewHelper.getFastViewOverlay(context)
    }

    override fun dismissFloatingController() {
        gameAboutModel.isShowGame(false)
    }

    override fun refreshScore() {
        gameMassageManager?.refreshScore()
    }

    fun onResume(){
        isEnableSound = true
    }

    fun onPause(){
        isEnableSound = false
    }

    fun onStop(){
        isEnableSound = false
    }


    interface OnSdkListener {
        fun customerServiceAction()

        fun historyOfBetAction()

        fun onEnterGame()

        fun onEnterLive(type: Int, msg: String)

        fun onLeaveLive(type: Int, str: String?)

        fun onLoginGame(i: Int, str: String?)

        fun onTokenLoseEffectiveness()

        /**
         * 游戏主界面切换的回调
         * isShowUp: true为打开，false为关闭
         */
        fun onGameFloatingDetailViewStatus(isShowUp: Boolean)

        fun onInsufficientBalance()

    }


    interface SocketStatesCallback{
        fun onOpen()
        fun onClose(isNeedReconnect: Boolean)
    }

    fun setSocketStatesCallback(callback: SocketStatesCallback){
        socketStatesCallback = callback;
    }


    interface BackgroundWatcher{
        fun OnSwitchToForeground()
        fun OnSwitchToBackground()
    }

}