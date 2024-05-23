package com.xcjh.app.websocket

import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.drake.engine.base.app
import com.google.gson.Gson
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.net.ApiComService.Companion.WEB_SOCKET_URL
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.onWsUserLogin
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.NoReadMsg
import com.xcjh.app.websocket.bean.PureFlowCloseBean
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.SendCommonWsBean
import com.xcjh.app.websocket.bean.UpdateLiveContentBean
import com.xcjh.app.websocket.listener.*
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author zobo101
 * 管理 webSocket
 */
class MyWsManager private constructor(private val mContext: Context) {

    val tag = "MyWsManager"
    var client: WebSocketClient? = null
    companion object {
        private var scheduledExecutorService: ScheduledExecutorService? = null
        private var errorNum = 0
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
        //    -------------------------------------websocket心跳检测------------------------------------------------
        /**
         * 每隔10秒进行一次对长连接的心跳检测
         */
        private const val HEART_BEAT_RATE = (10 * 1000).toLong()

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: MyWsManager? = null
        fun getInstance(context: Context): MyWsManager? {
            if (INSTANCE == null) {
                synchronized(MyWsManager::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MyWsManager(context)
                        if (scheduledExecutorService == null) {
                            val namedThreadFactory: ThreadFactory = object : ThreadFactory {
                                private val mCount = AtomicInteger(1)
                                override fun newThread(r: Runnable): Thread {
                                    return Thread(r, "JWebSocketClientService" + mCount.getAndIncrement())
                                }
                            }
                            scheduledExecutorService = ScheduledThreadPoolExecutor(
                                CORE_POOL_SIZE,
                                namedThreadFactory,
                                ThreadPoolExecutor.DiscardOldestPolicy()
                            )
                        }
                    }
                }
            }
            return INSTANCE
        }
    }

    // private var client: WebSocketClient? = null


    /**
     * 1.先初始化
     */
    fun initService() {
        try {
            initSocketClient()
            //开启心跳检测
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE)
        } catch (e: Exception) {
            "=======--initService------- ${e.message}".loge()
        }
    }

    /**
     * 断开重连后同步数据
     */
    private fun asyncInfo() {

    }

    fun stopService() {
        try {
            mContext.unregisterReceiver(receiver)
            cancelAll()
            closeHeartBeat()
            closeConnect()
            client = null
            INSTANCE = null
        } catch (e: Exception) {
            ("stopService: ===" + e.message).loge()
        }
    }

    /**
     * command
     *  5->6 登录成功         22->22 注销成功
     *  7->9 加入群聊成功       21->10 退出群聊成功   10通知群内人员用户退出直播间）
     *  13->13 心跳包成功
     * 32 -》禁言  bizId=""如果是空就是全局    不为空就是主播id
     * 33-》解除
     * 36->是被主播踢出直播间
     * 37直播间公告修改
     *  11->12->11 发送消息->发送消息成功->接收到消息
     *  19->20 获取指定群聊或好友历史或离线消息成功
     *  23->23 消息已读回复
     *  41 服务器推送消息【cmd:41】（当前用户被踢）
     */
    private fun parsingServiceLogin(msg: String) {
        val wsBean = jsonToObject2<ReceiveWsBean<Any>>(msg)
        //判读禁言是不是全局的
        val wsBeanDate = jsonToObject2<ReceiveWsBean<FeedSystemNoticeBean>>(msg)

        if(wsBean?.command==32||wsBean?.command==33){
            val feedMsgBeanDate = wsBeanDate?.data as FeedSystemNoticeBean
            if (wsBean?.command == 32&&feedMsgBeanDate.bizId.isEmpty() ) {
                Constants.ISSTOP_TALK = "1"
            }
            if (wsBean?.command == 33&&feedMsgBeanDate.bizId.isEmpty()) {
                Constants.ISSTOP_TALK = "0"
            }
        }

        if (wsBean?.code == "10114") {
            myToast(appContext.getString(R.string.no_chat_t),"",true)
            return
        }
        when (wsBean?.command) {
            6 -> {
                mLoginOrOutListener.forEach {
                    it.toPair().second.onLoginIn(wsBean.success == 0, wsBean)
                }
            }

            22 -> {
                mLoginOrOutListener.forEach {
                    it.toPair().second.onLoginOut(wsBean.success == 0, wsBean)
                }
            }

            9 -> {
                mLiveRoomListener.forEach {
                    it.toPair().second.onEnterRoomInfo(wsBean.success == 0, wsBean)
                }
            }

            10 -> {
                mLiveRoomListener.forEach {
                    it.toPair().second.onExitRoomInfo(wsBean.success == 0, wsBean)
                }
            }

            12 -> {
                //发送成功回调 10113登录
                mC2CListener.forEach {
                    it.toPair().second.onSendMsgIsOk(wsBean.success == 0, wsBean)
                }
                mLiveRoomListener.forEach {
                    it.toPair().second.onSendMsgIsOk(wsBean.success == 0, wsBean)
                }
                //退出登录
                if(wsBean.code.equals("10113")){
                    mNoReadMsgListener.forEach {
                        it.toPair().second.onUserIsKicked()

                    }
                }

            }

            13 -> {
                //心跳包成功

            }

            11 -> {
                //接收消息
                val wsBean2 = jsonToObject2<ReceiveWsBean<ReceiveChatMsg>>(msg)
                val chatMsgBean = wsBean2?.data as ReceiveChatMsg
                if (chatMsgBean.chatType == 1) {
                    //群聊
                    mLiveRoomListener.forEach {
                        it.toPair().second.onRoomReceive(chatMsgBean)
                    }
                } else {
                    //单聊
                    mC2CListener.forEach {
                        it.toPair().second.onC2CReceive(chatMsgBean)
                    }
                }
            }

            20 -> {//历史消息 http方式
                /* val s = wsBean.data as String
                 val string2map = string2map<ChatMsgBean>(s)
                 string2map?.keys
                 string2map?.values*/
            }

            23 -> {//消息已读
                mReadListener.forEach {
                    it.toPair().second.onReadReceive(wsBean)
                }
            }

            25 -> {//服务器主动推送直播间开播
                val wsBean2 = jsonToObject2<ReceiveWsBean<LiveStatus>>(msg)
                val chatMsgBean = wsBean2?.data as LiveStatus
                mLiveStatusListener.forEach {
                    it.toPair().second.onOpenLive(chatMsgBean)
                }
            }

            26 -> {//服务器主动推送直播间关播
                val wsBean2 = jsonToObject2<ReceiveWsBean<LiveStatus>>(msg)
                val chatMsgBean = wsBean2?.data as LiveStatus

                mOtherOffListenerListener.forEach{
                    it.toPair().second.onCloseLive(chatMsgBean)
                }



//                mLiveStatusListener.forEach {
//                    it.toPair().second.onCloseLive(chatMsgBean)
//                }




            }

            27 -> {//服务器主动推送直播间直播地址修改
                val wsBean2 = jsonToObject2<ReceiveWsBean<LiveStatus>>(msg)
                val chatMsgBean = wsBean2?.data as LiveStatus
                mLiveStatusListener.forEach {
                    it.toPair().second.onChangeLive(chatMsgBean)
                }
            }
            28-> {//服务器主动推送用户反馈通知消息
                val wsBean2 = jsonToObject2<ReceiveWsBean<FeedSystemNoticeBean>>(msg)
                val feedMsgBean = wsBean2?.data as FeedSystemNoticeBean
                mC2CListener.forEach {
                    it.toPair().second.onSystemMsgReceive(feedMsgBean)
                }

            }
            32  -> {//服务器主动推送用户反馈通知消息  禁言
                val wsBean2 = jsonToObject2<ReceiveWsBean<FeedSystemNoticeBean>>(msg)
                val feedMsgBean = wsBean2?.data as FeedSystemNoticeBean

                mC2CListener.forEach {
                    it.toPair().second.onSystemMsgReceive(feedMsgBean)
                }
                //就是主播直播间的禁言
                if(feedMsgBean.bizId.isNotEmpty()){
                    mLiveRoomListener.forEach {
                        it.toPair().second.onProhibition(feedMsgBean)
                    }
                }
            }


              33 -> {//服务器主动推送用户反馈通知消息  解禁
                val wsBean2 = jsonToObject2<ReceiveWsBean<FeedSystemNoticeBean>>(msg)
                val feedMsgBean = wsBean2?.data as FeedSystemNoticeBean

                mC2CListener.forEach {
                    it.toPair().second.onSystemMsgReceive(feedMsgBean)
                }
                //就是主播直播间的禁言
                if(feedMsgBean.bizId.isNotEmpty()){
                    mLiveRoomListener.forEach {
                        it.toPair().second.onOpeningUp(feedMsgBean)
                    }
                }
            }

            29 -> {//未读消息推送
                //接收消息
                val wsBean2 = jsonToObject2<ReceiveWsBean<NoReadMsg>>(msg)
                val chatMsgBean = wsBean2?.data as NoReadMsg
                mNoReadMsgListener.forEach {
                    it.toPair().second.onNoReadMsgNums(chatMsgBean.totalCount.toString())
                }
            }

            30 -> {
                val string = wsBean.data.toString()
                val wsBean2 = jsonToList<ReceiveChangeMsg>(string)
                mC2CListener.forEach {
                    it.toPair().second.onChangeReceive(wsBean2)
                }
                mLiveStatusListener.forEach {
                    it.toPair().second.onChangeReceive(wsBean2)
                }
                mOtherPushListener.forEach {
                    it.toPair().second.onChangeMatchData(wsBean2)
                }
            }
            34 -> {
                val wsBean2 = jsonToObject2<ReceiveWsBean<BeingLiveBean>>(msg)
                val chatMsgBean = wsBean2?.data as BeingLiveBean
                mOtherPushListener.forEach {
                    it.toPair().second.onAnchorStartLevel(chatMsgBean)
                }
            }
            35 -> {
                mOtherPushListener.forEach {
                    it.toPair().second.onNewsUpdateDate()
                }
            }
            36 -> {
                val wsBean2 = jsonToObject2<ReceiveWsBean<FeedSystemNoticeBean>>(msg)
                val feedMsgBean = wsBean2?.data as FeedSystemNoticeBean
                mLiveRoomListener.forEach {
                    it.toPair().second.onIsBlacklist(feedMsgBean)
                }
            }
            37 -> {//直播间公告修改
                val wsBean2 = jsonToObject2<ReceiveWsBean<UpdateLiveContentBean>>(msg)
                val feedMsgBean = wsBean2?.data as UpdateLiveContentBean
                mLiveRoomListener.forEach {
                    it.toPair().second.onUpdateLiveContent(feedMsgBean)
                }
            }
            42 -> {//纯净流关播
                Log.i("RRRRRR","收到消息========")
                val wsBean2 = jsonToObject2<ReceiveWsBean<PureFlowCloseBean>>(msg)
                val pureFlow = wsBean2?.data as PureFlowCloseBean
                Log.i("RRRRRR","转换数据========"+Gson().toJson(pureFlow))
                mC2CListener.forEach {
                    it.toPair().second.onPureFlowClose(pureFlow)
                }
            }

            43 -> {//纯净流开播
                val wsBean2 = jsonToObject2<ReceiveWsBean<LiveStatus>>(msg)
                val chatMsgBean = wsBean2?.data as LiveStatus
                mLiveStatusListener.forEach {
                    it.toPair().second.onOpenPureFlow(chatMsgBean)
                }
            }

            41 -> {//用户登录被踢

                mNoReadMsgListener.forEach {
                    it.toPair().second.onUserIsKicked()

                }
            }


            else -> {
                // 登录过期
                mContext.let {
                    // LoginAndOutUtil().logoutClick(it)
                }
            }
        }
    }
    /**
     * 发送消息
     */
    fun sendMessage(msg: String) {
        try {
            if (null != client && client?.isOpen == true) {
                msg.loge("===sendMessage==")
                client?.send(msg)

            }
        }catch (_:Exception){

        }
    }

    // private var mLoginOrOutListener: LoginOrOutListener? = null
    // private var mLiveRoomListener: LiveRoomListener? = null
    // private var mC2CListener: C2CListener? = null
    /**
     * 登录登出
     */
    private val mLoginOrOutListener = linkedMapOf<String, LoginOrOutListener>()
    fun setLoginOrOutListener(tag: String, listener: LoginOrOutListener) {
        mLoginOrOutListener[tag] = listener
    }

    fun removeLoginOrOutListener(tag: String) {
        if (mLoginOrOutListener[tag] != null) {
            mLoginOrOutListener.remove(tag)
        }
    }


    /**
     * 群消息
     */
    private val mLiveRoomListener = linkedMapOf<String, LiveRoomListener>()
    fun setLiveRoomListener(tag: String, listener: LiveRoomListener) {
        mLiveRoomListener[tag] = listener
    }

    fun removeLiveRoomListener(tag: String) {
        if (mLiveRoomListener[tag] != null) {
            mLiveRoomListener.remove(tag)
        }
    }

    private val mNoReadMsgListener = linkedMapOf<String, NoReadMsgPushListener>()
    fun setNoReadMsgListener(tag: String, listener: NoReadMsgPushListener) {
        mNoReadMsgListener[tag] = listener
    }

    fun removeNoReadMsgListener(tag: String) {
        if (mNoReadMsgListener[tag] != null) {
            mNoReadMsgListener.remove(tag)
        }
    }

    /**
     * 直播流状态
     */
    private val mLiveStatusListener = linkedMapOf<String, LiveStatusListener>()
    fun setLiveStatusListener(tag: String, listener: LiveStatusListener) {
        mLiveStatusListener[tag] = listener
    }

    fun removeLiveStatusListener(tag: String) {
        if (mLiveStatusListener[tag] != null) {
            mLiveStatusListener.remove(tag)
        }
    }


    /***
     * 单聊
     */
    private val mC2CListener = linkedMapOf<String, C2CListener>()
    fun setC2CListener(tag: String, listener: C2CListener) {
        mC2CListener[tag] = listener
    }

    fun removeC2CListener(tag: String) {
        if (mC2CListener[tag] != null) {
            mC2CListener.remove(tag)
        }
    }

    private val mReadListener = linkedMapOf<String, ReadListener>()


    /***
     * 消息已读状态
     */
    fun setReadListener(tag: String, listener: ReadListener) {
        mReadListener[tag] = listener
    }

    fun removeReadListener(tag: String) {
        if (mReadListener[tag] != null) {
            mReadListener.remove(tag)
        }
    }

    /**
     * 其他推送消息监听
     */
    private val mOtherPushListener = linkedMapOf<String, OtherPushListener>()
    fun setOtherPushListener(tag: String, listener: OtherPushListener) {
        mOtherPushListener[tag] = listener
    }

    fun removeOtherPushListener(tag: String) {
        if (mOtherPushListener[tag] != null) {
            mOtherPushListener.remove(tag)
        }
    }

    /**
     * 新建
     */
    private val mOtherOffListenerListener = linkedMapOf<String, MOffListener>()
    fun setOtherPushListener(tag: String, listener: MOffListener) {
        mOtherOffListenerListener[tag] = listener
    }

    fun removeMOtherOffListenerListener(tag: String) {
        if (mOtherOffListenerListener[tag] != null) {
            mOtherOffListenerListener.remove(tag)
        }
    }




    private var receiver: ChatMessageReceiver? = null



    /**
     * 动态注册广播
     */
    private fun doRegisterReceiver() {
        if (receiver == null) {
            receiver = ChatMessageReceiver()
        }
        val filter = IntentFilter(WebSocketAction.WEB_ACTION)
        mContext.registerReceiver(receiver, filter)
    }
    private inner class ChatMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("message") ?: return
            "onReceive====------------  $msg".loge()
//            parsingServiceLogin(msg)
            try {
                //appViewModel
                parsingServiceLogin(msg)
            } catch (e: Exception) {
                "======onReceive===webSocket解析异常------------  ${e.message}".loge()
            }
        }
    }
    /**
     * 初始化websocket连接
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun initSocketClient() {
        client = object : WebSocketClient(URI.create(WEB_SOCKET_URL)) {
            init {
                if (URI.create(WEB_SOCKET_URL).toString().contains("wss://")) {
                    trustAllHots(this)
                }
            }

            private fun trustAllHots(client: WebSocketClient) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MyWsClientCert().trustAllHots(client)
                }
                // val trustAllCerts = TrustManager
            }

            override fun onMessage(message: String) {
                val intent = Intent()
                intent.action = WebSocketAction.WEB_ACTION
                intent.putExtra("message", message)
                app.sendBroadcast(intent)
                // "onReceive====------------  $message".loge()
                /* try {
                     //appViewModel
                     parsingServiceLogin(message)
                 } catch (e: Exception) {
                     "======onReceive===webSocket解析异常------------  ${e.message}".loge()
                 }*/
            }

            override fun onOpen(handshakedata: ServerHandshake) {
                "websocket连接成功wsStatus===${appViewModel.wsStatusOpen.value}".loge("MyWsClient===")
                if (CacheUtil.isLogin()) {
                    GlobalScope.launch {
                        delay(2000)
                        onWsUserLogin() {}
                    }
                }

                appViewModel.wsStatusOpen.postValue(true)
                "websocket连接成功appViewModel_wsStatus=== ${appViewModel.wsStatusOpen.value}".loge("MyWsClient===")
                if (errorNum > 0) {
                    //Log.e("MyWsClient===", "-----------onOpen--------$errorNum")
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                appViewModel.wsStatusClose.postValue(true)
                "websocket 关闭appViewModel_wsStatus=== ${appViewModel.wsStatusClose.value}".loge("MyWsClient===")
                errorNum++
            }

            override fun onError(ex: java.lang.Exception?) {

            }
        }
        doRegisterReceiver()
        connect()
    }

    /**
     * 连接websocket
     */
    private fun connect() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService!!.schedule({
                try {
                    client?.connectionLostTimeout = 0
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client!!.connectBlocking()
                    //client!!.connect()
                    mHandler.postDelayed({
                        sendHeartCmd()
                    }, 500)

                } catch (e: Exception) {
                    e.printStackTrace()
                    "------connect-------- ${e.message}".loge()
                }
            }, 0, TimeUnit.SECONDS)
        }
    }

    private fun cancelAll() {
        if (scheduledExecutorService != null) {
            try {
                shutdownAndAwaitTermination(scheduledExecutorService!!)
            } catch (e: Exception) {
            }
            scheduledExecutorService = null
        }
    }

    private fun shutdownAndAwaitTermination(pool: ScheduledExecutorService) {
        pool.shutdown()
        try {
            if (!pool.awaitTermination(6, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow()
            }
        } catch (ie: InterruptedException) {
            pool.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    /**
     * 关闭心跳
     */
    private fun closeHeartBeat() {
        mHandler.removeCallbacks(heartBeatRunnable)
    }

    /**
     * 断开连接
     */
    private fun closeConnect() {
        try {
            if (null != client) {
                client!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
    }

    private val mHandler: Handler = Handler(Looper.myLooper()!!)
    private val heartBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            //"-----------心跳包检测连接状态client-----${client==null}".loge("wsService===")
            if (client != null) {
                ("-----------socket是否断开-----" + client!!.isClosed).loge("wsService===")
                if (client!!.isClosed) {
                    reconnectWs()
                    //MyWsManager.getInstance(appContext)?.stopService()
                    // MyWsManager.getInstance(appContext)?.initService()
                } else {
                    sendHeartCmd()
                }
            } else {
                //如果client已为空，重新初始化连接
                initSocketClient()
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE)
        }
    }

    fun sendHeartCmd() {
        try {
            //client.sendPing();
            if (client != null) {
                client?.send(Gson().toJson(SendCommonWsBean(cmd = 13, loginType = null)))
                //client?.sendPing()
            }
        } catch (e: Exception) {
            "-----------sendPing-----${e.message}".loge("wsService===")
        }
    }

    /**
     * 开启重连
     */
    private fun reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable)
        if (scheduledExecutorService != null) {
            scheduledExecutorService!!.schedule({
                try {
                    "-----------开启重连-----".loge("wsService===")
                    client!!.reconnect()
                    client!!.reconnectBlocking()
                } catch (e: InterruptedException) {
                    "-----------开启重连-----${ e.message}".loge("wsService===")
                }
            }, 0, TimeUnit.SECONDS)
        }
    }

}