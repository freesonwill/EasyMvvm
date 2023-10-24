package com.xcjh.app.websocket

import android.annotation.SuppressLint
import android.content.*
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.xcjh.app.R
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.LiveRoomListener
import com.xcjh.app.websocket.listener.LoginOrOutListener
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.app.websocket.listener.ReadListener
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.*
import org.java_websocket.client.WebSocketClient


/**
 * @author zobo101
 * 管理 webSocket
 */
class MyWsManager private constructor(private val mContext: Context) {

    val tag = "MyWsManager"

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: MyWsManager? = null
        fun getInstance(context: Context): MyWsManager? {
            if (INSTANCE == null) {
                //Log.e("====", "getInstance: ===" + null)
                synchronized(MyWsManager::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MyWsManager(context)
                    }
                }
            }
            //Log.e("====", "getInstance: ===!==" + null)
            return INSTANCE
        }
    }

    private var serviceIntent: Intent? = null
    private var client: WebSocketClient? = null
    private var binder: MyWsClientService.WsClientBinder? = null
    private var service: MyWsClientService? = null
    private var receiver: ChatMessageReceiver? = null


    /**
     * 1.先初始化
     */
    fun initService() {
        try {
            if (service == null) {
                startJWebSClientService()
                doRegisterReceiver()
            }
        } catch (e: Exception) {
            Log.e(tag, "=======--initService------- ${e.message}")
        }
    }

    /**
     * 断开重连后同步数据
     */
    private fun asyncInfo() {

    }

    fun stopService() {
        try {
            mContext.unbindService(serviceConnection)
            mContext.stopService(serviceIntent)
            mContext.unregisterReceiver(receiver)
            service = null
            client = null
            receiver = null
            INSTANCE = null
        } catch (e: Exception) {
            Log.e("===", "stopService: ===" + e.message)
        }
    }

    /**
     * 启动服务（webSocket客户端服务）
     */
    private fun startJWebSClientService() {
        if (serviceIntent == null) {
            serviceIntent = Intent(mContext, MyWsClientService::class.java)
        }
        mContext.startService(serviceIntent)
        // 绑定服务
        mContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

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

    /**
     * 绑定服务与活动
     */
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.e(tag, "=====服务与活动成功绑定")
            if (iBinder is MyWsClientService.WsClientBinder) {
                binder = iBinder
                service = binder?.service
                client = service?.client
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.e(tag, "====服务与活动成功断开")
        }
    }

    private inner class ChatMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("message") ?: return
            Log.e(tag, "onReceive====------------  $msg")
            try {
                //appViewModel
                parsingServiceLogin(msg)
            } catch (e: Exception) {
                Log.e(tag, "======onReceive===webSocket解析异常------------  ${e.message}")
            }
        }
    }

    /**
     * command
     *  5->6 登录成功         22->22 注销成功
     *  7->9 加入群聊成功       21->10 退出群聊成功
     *  13->13 心跳包成功
     *
     *
     *  11->12->11 发送消息->发送消息成功->接收到消息
     *  19->20 获取指定群聊或好友历史或离线消息成功
     *  23->23 消息已读回复
     */
    private fun parsingServiceLogin(msg: String) {
        val wsBean = jsonToObject2<ReceiveWsBean<Any>>(msg)
        if (wsBean?.code=="10114"){
            myToast(appContext.getString(R.string.no_chat_t))
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
                //发送成功回调
                mC2CListener.forEach {
                    it.toPair().second.onSendMsgIsOk(wsBean.success == 0, wsBean)
                }
                mLiveRoomListener.forEach {
                    it.toPair().second.onSendMsgIsOk(wsBean.success == 0, wsBean)
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
            else -> {
                // 登录过期
                mContext.let {
                    // LoginAndOutUtil().logoutClick(it)
                }
            }
        }
    }

    fun sendMessage(msg: String) {
        if (client?.isOpen == true) {
            msg.loge("===sendMessage==")
            service?.sendMsg(msg)
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
}