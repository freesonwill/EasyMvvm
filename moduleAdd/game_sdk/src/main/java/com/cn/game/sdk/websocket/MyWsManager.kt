package com.cn.game.sdk.websocket

import android.annotation.SuppressLint
import android.content.*
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.cn.game.sdk.common.GameResCode
import com.cn.game.sdk.game.GameMessage
import com.cn.game.sdk.game.GameMessageKuai
import com.cn.game.sdk.game.GameMessageKuaikt
import com.cn.game.sdk.game.GameResMessage
import com.cn.game.sdk.net.ApiComService.Companion.WEB_SOCKET_URL
import com.xcjh.app.websocket.WebSocketAction
import com.xcjh.base_lib.utils.*
import game.common.proto.ClientReq
import game.common.proto.ClientRes.ErrorMessage
import kotlinx.coroutines.*
import java.lang.Runnable
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author zobo101
 * 管理 webSocket
 */
class MyWsManager private constructor(private val mContext: Context) {
    val tag = "MyWsManager"
    var client: JWebSocketClient? = null
    lateinit var _gameMsg : GameMessage
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

        fun gameMessage(): GameMessage {
            return INSTANCE?.gameMessage() ?: GameMessageKuaikt(INSTANCE?.client!!);
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
     */
    private fun parsingServiceLogin(msg: String) {

    }
    /**
     * 发送消息
     */
    fun sendMessage(reqType: Int, msg: com.google.protobuf.GeneratedMessageV3) {
        try {
            if (null != client && client?.isOpen == true) {
                client?.sendMessage(reqType, msg.toByteArray())
            }
        }catch (_:Exception){

        }
    }

    fun  gameMessage(): GameMessage {
        return _gameMsg
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

                parsingServiceLogin(msg)
            } catch (e: Exception) {
                "======onReceive===webSocket解析异常------------  ${e.message}".loge()
            }
        }
    }


    public fun onMessage(mid:Int, sid:Int, byteArray: ByteArray){
        if(mid == 0){
            return
        }
        GlobalScope.launch {
            try {
                GameResMessage.onMessage(mid, sid, byteArray)
            }catch (e: Exception){
                "======onMessage===调用异常------------  ${e.message}".loge()
            }
        }
    }
    /**
     * 初始化websocket连接
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun initSocketClient() {
        var uri  = URI.create(WEB_SOCKET_URL)
        client = JWebSocketClient(this, uri)
        _gameMsg = GameMessageKuaikt(client!!)
//        client = object : WebSocketClient(URI.create(WEB_SOCKET_URL), Draft_6455()) {
//            init {
//                if (URI.create(WEB_SOCKET_URL).toString().contains("wss://")) {
//                    trustAllHots(this)
//                }
//            }
//
//            private fun trustAllHots(client: WebSocketClient) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    MyWsClientCert().trustAllHots(client)
//                }
//                // val trustAllCerts = TrustManager
//            }
//
//            override fun onMessage(message: String) {
//                val intent = Intent()
//                intent.action = WebSocketAction.WEB_ACTION
//                intent.putExtra("message", message)
//                app.sendBroadcast(intent)
//            }
//
//            override fun onMessage(bytes: ByteBuffer?) {
//                super.onMessage(bytes)
//                if (bytes == null){
//                    return
//                }
//
//                _gameMsg?.onMessage(bytes)
//            }
//
//            override fun onOpen(handshakedata: ServerHandshake) {
//                _gameMsg?.onLogin()
//                "websocket连接成功wsStatus===${appGameViewModel.wsStatusGameOpen.value}".loge("MyWsClient===")
////                if (CacheUtil.isLogin()) {
////                    GlobalScope.launch {
////                        delay(2000)
////                        onWsUserLogin() {}
////                    }
////                }
//
//                appGameViewModel.wsStatusGameOpen.postValue(true)
//                "websocket连接成功appViewModel_wsStatus=== ${appGameViewModel.wsStatusGameOpen.value}".loge("MyWsClient===")
//                if (errorNum > 0) {
//                    //Log.e("MyWsClient===", "-----------onOpen--------$errorNum")
//                }
//            }
//
//            override fun onClose(code: Int, reason: String, remote: Boolean) {
//                appGameViewModel.wsStatusGameClose.postValue(true)
//                "websocket 关闭appViewModel_wsStatus=== ${appGameViewModel.wsStatusGameClose.value}".loge("MyWsClient===")
//                errorNum++
//            }
//
//            override fun onError(ex: java.lang.Exception?) {
//
//            }
//        }
//        _gameMsg = GameMessage(client)
//        _gameMsg?.createChiper()
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

    /**
     * 发送心跳
     */
    fun sendHeartCmd() {
        try {
            //client.sendPing();
            if (client != null) {
//                client?.send(Gson().toJson(SendCommonWsBean(cmd = 13, loginType = null)))
                var req = ClientReq.PingBackReq.newBuilder().build()
                _gameMsg.ping(req)
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