package com.xcjh.app.websocket

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.google.gson.Gson
import com.xcjh.app.appViewModel
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.CacheUtil.isLogin
import com.xcjh.app.utils.onWsUserLogin
import com.xcjh.app.websocket.bean.SendCommonWsBean
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.loge
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author zobo101
 */
class MyWsClientService : Service() {
    var client: WebSocketClient? = null
    private val mBinder = WsClientBinder()
    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var errorNum = 0

    /**
     * 用于Activity和service通讯
     */
    inner class WsClientBinder : Binder() {
        val service: MyWsClientService
            get() = this@MyWsClientService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //初始化websocket
        initSocketClient()
        //开启心跳检测
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE)
        return START_STICKY
    }

    override fun onDestroy() {
        closeConnect()
        cancelAll()
        closeHeartBeat()
        super.onDestroy()
    }

    /**
     * 初始化websocket连接
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun initSocketClient() {
        client = object : WebSocketClient(URI.create(WebSocketAction.WEB_SOCKET_URL)) {
            init {
                if (URI.create(WebSocketAction.WEB_SOCKET_URL).toString().contains("wss://")) {
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
                sendBroadcast(intent)
            }

            override fun onOpen(handshakedata: ServerHandshake) {
                "websocket连接成功wsStatus===${appViewModel.wsStatus.value}".loge("MyWsClient===")
                if (isLogin()) {
                    GlobalScope.launch {
                        delay(2000)
                        onWsUserLogin() {}
                    }

                }
                appViewModel.wsStatus.postValue(1)
                "websocket连接成功appViewModel_wsStatus=== ${appViewModel.wsStatus.value}".loge("MyWsClient===")
                if (errorNum > 0) {
                    //Log.e("MyWsClient===", "-----------onOpen--------$errorNum")
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                appViewModel.wsStatus.postValue(2)
                "websocket 关闭 $reason  $remote".loge("MyWsClient===")
                "websocket 关闭appViewModel_wsStatus=== ${appViewModel.wsStatus.value}".loge("MyWsClient===")
                errorNum++
            }

            override fun onError(ex: java.lang.Exception?) {

            }
        }
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
     * 发送消息
     */
    fun sendMsg(msg: String?) {
        try {
            if (null != client && client?.isOpen == true) {
                client?.send(msg)
            }
        }catch (e :Exception){

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

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
        //    -------------------------------------websocket心跳检测------------------------------------------------
        /**
         * 每隔10秒进行一次对长连接的心跳检测
         */
        private const val HEART_BEAT_RATE = (10 * 1000).toLong()
    }
}