package com.xcjh.app.websocket

import android.app.Service
import android.content.Intent
import android.os.*
import com.google.gson.Gson
import com.xcjh.app.appViewModel
import com.xcjh.app.net.ApiComService.Companion.WEB_SOCKET_URL
import com.xcjh.app.utils.CacheUtil.isLogin
import com.xcjh.app.utils.onWsUserLogin
import com.xcjh.app.websocket.bean.SendCommonWsBean
import com.xcjh.base_lib.utils.loge
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.*

/**
 * onStartService和onBind同时调用
 * onCreate方法只会调用一次  onBind onStartCommand方法调用多次
 * 1、先调用startService再调用onBind,
 * onCreate() --onStartCommand()--onBind--onUnBind--onDestroy
 * 2、先调用onBind再调用startService
 * onCreate() --onBind()--onStartCommand--onUnBind--onDestroy
 *
 * @author
 */
class MyWsClientService : Service() {
    lateinit var client: WebSocketClient
    private lateinit var mBinder : WsClientBinder
    private var errorNum = 0

    companion object {
        //    -------------------------------------websocket心跳检测------------------------------------------------
        /**
         * 每隔10秒进行一次对长连接的心跳检测
         */
        private const val HEART_BEAT_RATE = (10 * 1000).toLong()
    }
    /**
     * 用于Activity和service通讯
     */
    inner class WsClientBinder : Binder() {
        fun getService(): MyWsClientService {
            return this@MyWsClientService
        }
    }

    override fun onCreate() {
        super.onCreate()
        mBinder = WsClientBinder()
        initSocketClient()
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY//系统强制杀掉之后，Service依然设置为started状态，再次重新创建该Service，Intent参数为null
    // START_NOT_STICKY(不会重新创建该Service)
    // START_REDELIVER_INTENT (系统强制杀掉之后会再次重新创建该Service,intent一定不是null)
    }

    override fun onDestroy() {
        "=====服务onDestroy".loge()
        closeConnect()
        closeHeartBeat()
        super.onDestroy()
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
                "onMessage====------------  $message".loge()
                val intent = Intent()
                intent.action = WebSocketAction.WEB_ACTION
                intent.putExtra("message", message)
                sendBroadcast(intent)
            }
            override fun onOpen(handshakedata: ServerHandshake) {
                "websocket连接成功wsStatus===${appViewModel.wsStatusOpen.value}".loge("MyWsClient===")
                //开启心跳检测
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE)
                if (isLogin()) {
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
        connect()
    }

    /**
     * 连接websocket
     */
    private fun connect() {
        try {
            client.connectionLostTimeout = 0
            //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
            client.connectBlocking()
            //client!!.connect()
        } catch (e: Exception) {
            e.printStackTrace()
            "------connect-------- ${e.message}".loge()
        }
    }


    /**
     * 发送消息
     */
    fun sendMsg(msg: String?) {
        try {
            if (client.isOpen) {
                client.send(msg)
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
            client.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    private val mHandler: Handler = Handler(Looper.myLooper()!!)
    private val heartBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            //"-----------心跳包检测连接状态client-----${client==null}".loge("wsService===")
            ("-----------socket是否断开-----" + client.isClosed).loge("wsService===")
            if (client.isClosed) {
                reconnectWs()
            } else {
                sendHeartCmd()
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE)
        }
    }

    fun sendHeartCmd() {
        try {
            //client.sendPing();
            client.send(Gson().toJson(SendCommonWsBean(cmd = 13, loginType = null)))
        } catch (e: Exception) {
            "-----------sendPing-----${e.message}".loge("wsService===")
        }
    }

    /**
     * 开启重连
     */
    private fun reconnectWs() {
        //closeHeartBeat()
        try {
            "-----------开启重连-----".loge("wsService===")
            client.reconnect()
            client.reconnectBlocking()
        } catch (e: InterruptedException) {
            "-----------开启重连-----${ e.message}".loge("wsService===")
        }
    }


}