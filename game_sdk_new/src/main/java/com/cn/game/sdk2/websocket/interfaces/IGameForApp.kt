package com.cn.game.sdk2.websocket.interfaces

import android.content.Context
import android.view.View
import com.cn.game.sdk2.websocket.imp.GameApp

interface IGameForApp {
    /***
        1.加载sdk
            1、context: 应用的context
            2、lifecycleEnable: 用于弹出游戏窗口的Activity是否实现LifecycleOwner
            若实现这里传true，sdk会自动在页面切换前后台的时候开关声音
            若传false，需要应用在页面切换前后台调用sdk接口
            3、gameServer：sdk游戏服的地址，如果传空，sdk会用默认地址
            4、logServer：sdk日志地址，如果传空，sdk会用默认地址
            5、onSdkListener：游戏的回调都在这里实现
     */
    fun loadGame(context: Context, lifecycleEnable: Boolean, gameServer:String, logServer:String, onSdkListener: GameApp.OnSdkListener)

    //fun removeSdkListener()

    /***
        2.登录游戏
            1、token: 用户token
            2、agentName: 平台名称
            3、是否主播
            4、当主播的时候，是否不显示更多里的：客服和投注记录按钮
     */
    fun login(token: String, agentName: String,isAnchor: Boolean, simplifyMoreButtons:Boolean)

    /***
        3.进入直播间
            1、liveId: 直播间id
            2、miniGameIds: 预设的游戏ids
            3、value: 透传数据
            如果是切换直播间则直接调用该方法会离开前面直播间到达新直播间
     */
    fun enterLive(liveId: String, gameIds: List<Int>, data: String)

    /***
        4.离开直播间:用于主动退出离开直播间（切换直播间不调）
     */
    fun leaveLive()

    /***
        5.注销sdk:用于主动退出离开直播间（切换直播间不调）
     */
    fun cancelGame()

    /***
        6.是否允许下注
            1、isAllow: 不调该接口的时候，默认true
     */
    fun allowedBet(isAllow: Boolean)

    /***
        7.创建并获取游戏浮窗视图
            1、context: activity的context
            此为游戏浮窗，点击后会打开游戏界面
            需要在进入游戏前调用，该方法会new floatEnterView，所以只调用一次
     */
    fun createFloatEnterView(context:Context): View

    /***
        8.创建并获取游戏结果视图
            1、context: activity的context
            此为游戏结果视图，点击无反应，游戏界面打开会隐藏
            需要在进入游戏前调用，该方法会new floatResultView，所以只调用一次
     */
    fun createFloatResultView(context:Context): View

    /***
        9.隐藏游戏主窗口
     */
    fun dismissFloatingController()

    /***
        10.当游戏依附的activity在onResume的时候调用（仅在loadGame方法的lifecycleEnable参数传false的时候才需要调）
     */
    fun onResume()

    /**
        11.当游戏依附的activity在onPause的时候调用（仅在loadGame方法的lifecycleEnable参数传false的时候才需要调）
     */
    fun onPause()

    /**
        12.当游戏依附的activity在onStop的时候调用（仅在loadGame方法的lifecycleEnable参数传false的时候才需要调)
     */
    fun onStop()

    /**
        13.刷新余额
     */
    fun refreshScore()

    /**
        14.打开直播间里指定gameid的游戏界面（在直播间里点击跟投的时候可以调用，目前只有快三一款游戏，所以暂时只弹出快三的游戏界面）
     */
    fun openGameDialog(miniGameId:Int)
    
}