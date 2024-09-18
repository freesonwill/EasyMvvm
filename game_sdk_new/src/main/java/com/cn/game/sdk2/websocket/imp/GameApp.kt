package com.cn.game.sdk2.websocket.imp

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.SortedList
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.moduleList
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.GsonUtils
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appContext
import com.cn.game.sdk2.websocket.appLifecycleEnable
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.gameMassageManager
import com.cn.game.sdk2.websocket.interfaces.IGameForApp
import com.cn.game.sdk2.websocket.isEnableSound
import com.cn.game.sdk2.websocket.isNeedReconnect
import com.google.gson.reflect.TypeToken
import com.xcjh.base_lib2.ModuleInitializer
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import game.common.proto.ClientReq
import game.mod.proc.yf.proto.req.GameReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent


/**
 * 提供给app调用的方法
 * 文档见 
 * @see IGameForApp ：由于商户要求与另一java统一，从单例改用静态方法调用
 */

class GameApp  private constructor(){
    companion object{
        /************************************* Method *************************************/

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

        @JvmStatic
         fun loadGame(
            context: Context,
            lifecycleEnable: Boolean,
            gameServer: String,
            logServer: String,
            onSdkListener: OnSdkListener
        ) {
            appContext = context
            ModuleInitializer.application = context.applicationContext as Application
            appLifecycleEnable = lifecycleEnable
            appListener = onSdkListener
            GameSocketManager.getInstance()?.initSocketClient(gameServer)
            koinApplication.apply {
                androidContext(context)
                modules(moduleList)
            }
            //todo:logServer
            (appContext as Application).registerActivityLifecycleCallbacks(object :
                ActivityLifecycleCallbacks {
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

        /* fun removeSdkListener() {
            appListener = null
        }*/

        /** 登录
         * - Parameter agentName: 平台名称
         * - Parameter token: 用户token
         * - type ==1 成功 type =1000（desc：您当前还在其他游戏中）type =1001 （desc：token验证失败）type =1002 （desc：余额不足）type =1005（desc：当前服务器正在维护）type =200（desc：其他情况）
         * - 初始化流程：1）App调用登录：loadGame + loginGameWithAgentName ——>2）进入房间坐下：GameServiceImp.enterInfo()
         * - ——>3)App进入直播间:GameServiceImp.groupInfo() ——>4)进入小游戏:GameServiceImp.gameInfo()
         */
        //platform= 6 ,requestId = 0,version = "1"
        @JvmStatic
         fun login(
            token: String,
            agentName: String,
            isAnchor: Boolean,
            simplifyMoreButtons: Boolean
        )  {
            if (gameAboutModel.isOpen) {
                "login token:$token".loge()
                gameAboutModel.token = token
                gameAboutModel.isAnchor = isAnchor
                gameAboutModel.agentName = agentName
                gameAboutModel.simplifyMoreButtons = simplifyMoreButtons
                val req = ClientReq.LoginReq.newBuilder().setPlatform(6).setRequestId(0).setVersion("1")
                    .setNickname("").setAgentName(agentName).setToken(token).build()
                gameMassageManager?.login(req)
            } else {
                "login-->socket is not connect，please wait for a successful connection before attempting to login".loge()
            }
        }

        /** 进入直播間
         * - Parameter liveId: 直播間id
         * - Parameter gIds: 遊戲ids
         * - Parameter data_p: 透传资料（转抛）
         * - type ==1 成功 随便
         */
        //1213,3
        @JvmStatic
         fun enterLive(liveId: String, gameIds: List<Int>, data: String) {
            "enterLive".loge()
            val req = GameReq.EnterGroup.newBuilder()
            gameIds.forEach {
                req.addMiniGameIds(it)
            }
            val build = req.setData(data).setId(liveId).build()
            gameAboutModel.liveId = liveId
            gameAboutModel.gameIds = gameIds
            gameAboutModel.data = data
            gameMassageManager?.enterGroup(build)
            //测试直接进入游戏
            /*gameMassageManager?.enterGame(
                GameReq.EnterMiniGame.newBuilder().setMiniGameId(gameIds[0]).build()
            )*/
        }

        /** 离开直播間
         * - Parameter liveId: 直播間id
         */
        @JvmStatic fun leaveLive() {
            gameMassageManager?.levelGroup()
            ThreadUtils.mainScope.launch {
                ViewHelper.instance.clearAllView()
            }
        }

        /**
         * 注销游戏
         */
        @JvmStatic
         fun cancelGame() {
            isNeedReconnect = false
            appContext = null
            appListener = null
            GameSocketManager.getInstance()?.stopService()
        }

        /**
         * 是否彈出遊戲框
         */

        /**
         * 是否允許下注
         * - Parameter isAllow: 默認true
         */
        @JvmStatic
         fun allowedBet(isAllow: Boolean) {
            gameAboutModel.isAllowedBet(isAllowedBet = isAllow)
        }

        /**
         * 入口漂浮窗視圖
         */
        @JvmStatic
         fun createFloatEnterView(context: Context): View {
            if (context is LifecycleOwner && appLifecycleEnable) {
                (context as LifecycleOwner).lifecycle.addObserver(object : LifecycleEventObserver {
                     override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        when (event) {
                            Lifecycle.Event.ON_RESUME -> {
                                resumeGame()
                            }

                            Lifecycle.Event.ON_PAUSE -> {
                                pauseGame()
                            }

                            Lifecycle.Event.ON_STOP -> {
                                pauseGame()
                            }

                            else -> {

                            }
                        }
                    }

                })
            }
            return ViewHelper.instance.getGameEnterView(context)
        }

        internal fun resumeGame(){
            isEnableSound = true
        }

        internal fun pauseGame(){
            //todo：暂停动画等任务
            isEnableSound = false
        }


        /**
         * 结果视图
         */
        @JvmStatic
         fun createFloatResultView(context: Context): View {
            return ViewHelper.instance.getFastViewOverlay(context)
        }

        @JvmStatic
         fun dismissFloatingController() {
            ThreadUtils.mainScope.launch {
                gameAboutModel.isShowGame(false)
            }
        }

        @JvmStatic
         fun onResume() {
            resumeGame()
        }

        @JvmStatic
         fun onPause() {
            pauseGame()
        }

        @JvmStatic
         fun onStop() {
            pauseGame()
        }

        @JvmStatic
         fun refreshScore() {
            gameMassageManager?.refreshScore()
        }

        /**
         * 打开游戏对话框
         * @param gameId: 游戏id
         * @param isGameList: 是否打开游戏大厅,true-打开,false-不打开
         */
        @JvmStatic
        fun openGameDialog(miniGameId: Int,isGameList:Boolean) {
             ThreadUtils.mainScope.launch {
                 gameAboutModel.isShowGame(true, miniGameId, isGameList)
             }
        }

        /**
         * * 传入wali游戏接口
            传入json字符串 json 格式:
            [{
            "idp":1,            //游戏Id
            "gameType":1,      //游戏类型
            "name":"捕鱼",      //游戏名称
            "weight":1,        //权重排序
            "direction":1,     //屏幕方向
            "icon":"icon地址"   //icon地址
            }]
            GAME_TYPE_BUYU(1),      3
            GAME_TYPE_SHIXUN(2),    2
            GAME_TYPE_QIPAI(3),     1
            GAME_TYPE_DIANZI(4),    5
            GAME_TYPE_SPORTS(5),    4
         */
        @JvmStatic
        fun setMoreGames(moreGameList: String) {
            ThreadUtils.mainScope.launch {
                val gameList = withContext(Dispatchers.IO){
                    val gameList = GsonUtils.fromJson<SortedList<GameHallItem>>(moreGameList,object : TypeToken<SortedList<GameHallItem>>(){}.type)
                    for(i in gameList.size-1 downTo  0) {
                        if(gameList[i].gameType <= 0 )gameList.removeAt(i)
                    }
                    gameList.changeComparator { o1, o2 ->
                        val gameType1 = GameHallItem.gameType2Index(o1.gameType)
                        val gameType2 = GameHallItem.gameType2Index(o2.gameType)
                        when(val it = gameType1.compareTo(gameType2)){
                            0 -> o1.weight.compareTo(o2.weight)
                            else -> it
                        }
                    }
                    gameList.addAll(
                        listOf(
                            GameHallItem(0,0,1,0, R.mipmap.game_sdk_kuai_icon_logo.toString(),"快三"),
                            GameHallItem(1,0,0, 0,R.mipmap.game_sdk_kuai_icon_logo.toString(),"快三2"),
                        )
                    )
                    gameList
                }
                gameAboutModel.setMoreGames(gameList)
            }
        }

        /**
         * socket是否连接上
         */
        @JvmStatic
        val isSocketConnected get() = gameAboutModel.isOpen

        internal val koinApplication by lazy { KoinApplication.init() }



    }


    interface OnSdkListener {
        /**
         * socket已连接
         */
        @UiThread fun onSocketConnected() {}
        /**
         * socket已关闭
         */
        @UiThread fun onSocketClosed() {}
        /**
         * 登录游戏
         * type: 1为成功. 其他为失败
         * msg: 错误信息,只在失败时有值
         */
        @UiThread fun onLoginGame(type: Int, msg: String?)

        /**
         * 进入直播间
         * type: 1为成功. 其他为失败
         * msg: 错误信息,只在失败时有值
         */
        @UiThread fun onEnterLive(type: Int, msg: String)

        /**
         * 进入游戏
         */
        @UiThread fun onEnterGame()

        /**
         * 离开直播间
         * liveId: 直播间id
         * type: 1为成功. 其他为失败
         * msg: 错误信息,只在失败时有值
         */
        @UiThread fun onLeaveLive(liveId: String,type: Int, msg: String?)

        /**
         * token失效
         */
        @UiThread fun onTokenLoseEffectiveness()

        /**
         * 游戏主界面切换的回调
         * isShowUp: true为打开，false为关闭
         */
        @UiThread fun onGameFloatingDetailViewStatus(isShowUp: Boolean)

        /**
         * 点击投注记录
         */
        @UiThread fun onHistoryOfBetAction()

        /**
         * 点击客服
         */
        @UiThread fun onCustomerServiceAction()

        /**
         * 余额不足
         */
        @UiThread fun onInsufficientBalance()

        /**
         * 点击游戏大厅里除了sdk本身游戏外的回调
         * data: 游戏item的json
         */
        @UiThread fun onClickOtherGameWithBlock(json:String)
    }


    internal interface GameSdkKoinComponent : KoinComponent {
        override fun getKoin(): Koin {
            return koinApplication.koin
        }
    }

}