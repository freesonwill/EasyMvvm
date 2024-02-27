package com.xcjh.app.websocket.so;

import static com.xcjh.app.net.ApiComService.WEB_SOCKET_URL;
import static com.xcjh.base_lib.App.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;

import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xcjh.app.MyApplicationKt;
import com.xcjh.app.R;
import com.xcjh.app.bean.BeingLiveBean;
import com.xcjh.app.utils.CacheUtil;
import com.xcjh.app.utils.SocketHelperKt;
import com.xcjh.app.websocket.WebSocketAction;
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean;
import com.xcjh.app.websocket.bean.LiveStatus;
import com.xcjh.app.websocket.bean.NoReadMsg;
import com.xcjh.app.websocket.bean.ReceiveChangeMsg;
import com.xcjh.app.websocket.bean.ReceiveChatMsg;
import com.xcjh.app.websocket.bean.ReceiveWsBean;
import com.xcjh.app.websocket.bean.SendCommonWsBean;
import com.xcjh.app.websocket.listener.C2CListener;
import com.xcjh.app.websocket.listener.LiveRoomListener;
import com.xcjh.app.websocket.listener.LiveStatusListener;
import com.xcjh.app.websocket.listener.LoginOrOutListener;
import com.xcjh.app.websocket.listener.NoReadMsgPushListener;
import com.xcjh.app.websocket.listener.OtherPushListener;
import com.xcjh.app.websocket.listener.ReadListener;
import com.xcjh.base_lib.AppKt;
import com.xcjh.base_lib.Constants;
import com.xcjh.base_lib.utils.HelperKt;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MyWsJavaManager{
    Context mContext;
    private static volatile MyWsJavaManager INSTANCE;

    private static ScheduledExecutorService scheduledExecutorService= null;
    private int errorNum = 0;
    private static final  int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    /**
     * 每隔10秒进行一次对长连接的心跳检测
     */
    private static final long HEART_BEAT_RATE = (long) (10 * 1000);
    //webSocket
    WebSocketClient client= null;

    // 私有构造函数，防止外部实例化
    private MyWsJavaManager(Context context) {
        mContext=context;
    }

    public static MyWsJavaManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MyWsJavaManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MyWsJavaManager(context);
                    if (scheduledExecutorService == null) {
                        ThreadFactory namedThreadFactory= new ThreadFactory() {
                            AtomicInteger mCount =new AtomicInteger(1);

                            @Override
                            public Thread newThread(Runnable runnable) {
                                return new Thread(runnable, "JWebSocketClientService" + mCount.getAndIncrement());
                            }
                        };

                        scheduledExecutorService =new ScheduledThreadPoolExecutor(
                                CORE_POOL_SIZE,
                                namedThreadFactory,
                                new ThreadPoolExecutor.DiscardOldestPolicy()
                        );

                    }
                }
            }
        }
        return INSTANCE;
    }
    /**
     * 1.先初始化
     */
    public  void  initService(){
        try {
            initSocketClient();
            //开启心跳检测
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        }catch (Exception e){
            Log.i("=======err_initService-------",e.getMessage());
        }

    }

    /**
     * 初始化websocket连接
     */
    private void initSocketClient(){

        client=new WebSocketClient(URI.create(WEB_SOCKET_URL)) {
            {
                if (URI.create(WEB_SOCKET_URL).toString().contains("wss://")) {
                    trustAllHots(this);
                }
            }
            private void trustAllHots(WebSocketClient client) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                   new MyJavaWsClientCert().trustAllHots(client);
                }
                // val trustAllCerts = TrustManager
            }


            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("websocket连接成功wsStatus===", "=="+MyApplicationKt.getAppViewModel().getWsStatusOpen().getValue());
                if (CacheUtil.INSTANCE.isLogin()) {

                    Thread thread = new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            //    onWsUserLogin() {}
                            SocketHelperKt.onWsUserLogin(new Function1<Boolean, Unit>() {
                                @Override
                                public Unit invoke(Boolean aBoolean) {

                                    return null;
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    thread.start();
                    MyApplicationKt.getAppViewModel().getWsStatusOpen().setValue(true);
                    Log.i("websocket连接成功wsStatus===", "=="+MyApplicationKt.getAppViewModel().getWsStatusOpen().getValue());

                    if (errorNum > 0) {
                        //Log.e("MyWsClient===", "-----------onOpen--------$errorNum")
                    }
                }
            }

            @Override
            public void onMessage(String message) {
                Intent intent =new Intent();
                intent.setAction( WebSocketAction.WEB_ACTION) ;
                intent.putExtra("message", message);
                app.sendBroadcast(intent);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                MyApplicationKt.getAppViewModel().getWsStatusClose().setValue(true);
                Log.i("\"websocket 关闭appViewModel_wsStatus=== ", "=="+MyApplicationKt.getAppViewModel().getWsStatusClose().getValue());
                errorNum++;
            }

            @Override
            public void onError(Exception ex) {

            }
        };

        doRegisterReceiver();
        connect();
    }

    private ChatMessageReceiver receiver=null;

    /**
     * 动态注册广播
     */
    private void doRegisterReceiver(){
        if (receiver == null) {
            receiver =new ChatMessageReceiver();
        }
        IntentFilter filter =new IntentFilter(WebSocketAction.WEB_ACTION);
        mContext.registerReceiver(receiver, filter);

    }

    private Handler mHandler = new Handler(Looper.myLooper());

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                Log.d("wsService===", "-----------socket是否断开-----" + client.isClosed());
                if (client.isClosed()) {
                    reconnectWs();
                } else {
                    sendHeartCmd();
                }
            } else {
                initSocketClient();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        if (scheduledExecutorService != null) {
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("wsService===", "-----------开启重连-----");

                        client.reconnect();
                        client.reconnectBlocking();

                    }catch (Exception exception){

                    }

                }
            }, 0, TimeUnit.SECONDS);
        }
    }

    /**
     * 连接websocket
     */
    private void connect(){
        if (scheduledExecutorService != null) {
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.setConnectionLostTimeout(0);
                        //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                        client.connectBlocking();
                        //client!!.connect()
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendHeartCmd();
                            }
                        }, 500);

                    }catch (Exception exception){

                    }

                }
            }, 0, TimeUnit.SECONDS);

        }

    }

    void sendHeartCmd() {
        try {
            //client.sendPing();
            if (client != null) {
                client.send(new Gson().toJson(new SendCommonWsBean( 13,null,"","3","","","")));
                //client?.sendPing()
            }
        } catch (Exception e) {
//            "-----------sendPing-----${e.message}".loge("wsService===")
            Log.i("-----------sendPing----","="+e.getMessage());
        }
    }


    private class ChatMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            if (msg == null) {
                return;
            }
            Log.e("onReceive====------------", "" + msg);
            try {
                parsingServiceLogin(msg);
            } catch (Exception e) {
                Log.e("======onReceive===webSocket解析异常------------", "=" + e.getMessage());
            }
        }
    }
    /**
     * 登录登出
     */
    private Map<String, LoginOrOutListener> mLoginOrOutListener = new LinkedHashMap<>();

    void setLoginOrOutListener(String tag  , LoginOrOutListener listener) {
        mLoginOrOutListener.put(tag,listener);
    }

    void removeLoginOrOutListener(String tag  ) {
        if (mLoginOrOutListener.get(tag) != null) {
            mLoginOrOutListener.remove(tag);
        }
    }

    /**
     * 群消息
     */
    private Map<String, LiveRoomListener> mLiveRoomListener = new LinkedHashMap<>();

    void setLiveRoomListener(String tag  , LiveRoomListener listener) {
        mLiveRoomListener.put(tag,listener);
    }

    void removeLiveRoomListener(String tag) {
        if (mLiveRoomListener.get(tag) != null) {
            mLiveRoomListener.remove(tag);
        }
    }


    /***
     * 单聊
     */
    private Map<String, C2CListener> mC2CListener = new LinkedHashMap<>();
    void setC2CListener(String tag , C2CListener listener) {
        mC2CListener.put(tag,listener);
    }

    void removeC2CListener(String tag ) {
        if (mC2CListener.get(tag) != null) {
            mC2CListener.remove(tag);
        }
    }


    private Map<String, ReadListener> mReadListener =new LinkedHashMap<>();
    /***
     * 消息已读状态
     */
    void setReadListener(String tag  , ReadListener listener) {
        mReadListener.put(tag,listener);
    }

    void removeReadListener(String tag) {
        if (mReadListener.get(tag) != null) {
            mReadListener.remove(tag);
        }
    }

    /**
     * 直播流状态
     */
    private Map<String, LiveStatusListener> mLiveStatusListener =new LinkedHashMap<>();
    void setLiveStatusListener(String tag, LiveStatusListener listener) {
        mLiveStatusListener.put(tag,listener);
    }

    void removeLiveStatusListener(String tag) {
        if (mLiveStatusListener.get(tag) != null) {
            mLiveStatusListener.remove(tag);
        }
    }

    private Map<String, NoReadMsgPushListener> mNoReadMsgListener = new LinkedHashMap<>();
    void setNoReadMsgListener(String tag, NoReadMsgPushListener listener) {
        mNoReadMsgListener.put(tag,listener);
    }

    void removeNoReadMsgListener(String tag) {
        if (mNoReadMsgListener.get(tag) != null) {
            mNoReadMsgListener.remove(tag);
        }
    }


    /**
     * 其他推送消息监听
     */
    private Map<String, OtherPushListener> mOtherPushListener = new LinkedHashMap<>();
    void setOtherPushListener(String tag, OtherPushListener listener) {
        mOtherPushListener.put(tag,listener);
    }

    void removeOtherPushListener(String tag) {
        if (mOtherPushListener.get(tag) != null) {
            mOtherPushListener.remove(tag);
        }
    }

    /**
     * 发送消息
     */
    void sendMessage(String msg) {
        try {
            if (null != client && client.isOpen() == true) {
                Log.i("===sendMessage==",msg);
                client.send(msg);
            }
        }catch (Exception e){

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
    private void parsingServiceLogin(String msg) {
        JsonConvert jsonConvert=new JsonConvert();
        ReceiveWsBean wsBean= jsonConvert.jsonToObject2(msg,ReceiveWsBean.class);
        if (wsBean.getCommand()  == 32) {
            Constants.Companion.setISSTOP_TALK("1");
        }
        if (wsBean.getCommand() == 33) {
            Constants.Companion.setISSTOP_TALK("0");
        }
        if (wsBean.getCode().equals("10114")) {
            HelperKt.myToast( AppKt.getAppContext().getString(R.string.no_chat_t),"",true, Gravity.CENTER);
            return;
        }

        switch (wsBean.getCommand()){
            case 6:
                for (Map.Entry<String, LoginOrOutListener> entry : mLoginOrOutListener.entrySet()) {
                    String key = entry.getKey();
                    LoginOrOutListener listener = entry.getValue();
                    listener.onLoginIn(wsBean.getSuccess() == 0, wsBean);
                }
            break;
            case 22 :
                for (Map.Entry<String, LoginOrOutListener> entry : mLoginOrOutListener.entrySet()) {
                    String key = entry.getKey();
                    LoginOrOutListener listener = entry.getValue();
                    listener.onLoginOut(wsBean.getSuccess() == 0, wsBean);
                }

            break;
            case 9 :
                for (Map.Entry<String, LiveRoomListener> entry : mLiveRoomListener.entrySet()) {
                    String key = entry.getKey();
                    LiveRoomListener listener = entry.getValue();
                    listener.onEnterRoomInfo(wsBean.getSuccess() == 0, wsBean);
                }
            break;
            case 10:
                for (Map.Entry<String, LiveRoomListener> entry : mLiveRoomListener.entrySet()) {
                    String key = entry.getKey();
                    LiveRoomListener listener = entry.getValue();
                    listener.onExitRoomInfo(wsBean.getSuccess() == 0, wsBean);

                }

            break;
            case 12:
                //发送成功回调
                for (Map.Entry<String, C2CListener> entry : mC2CListener.entrySet()) {
                    String key = entry.getKey();
                    C2CListener listener = entry.getValue();
                    listener.onSendMsgIsOk(wsBean.getSuccess() == 0, wsBean);
                }
                for (Map.Entry<String, LiveRoomListener> entry : mLiveRoomListener.entrySet()) {
                    String key = entry.getKey();
                    LiveRoomListener listener = entry.getValue();
                    listener.onSendMsgIsOk(wsBean.getSuccess() == 0, wsBean);
                }

            break;
            case 13:
                    //心跳包成功
            break;
            case 11:
                //接收消息
                ReceiveWsBean<ReceiveChatMsg> receiveWsBean = (ReceiveWsBean<ReceiveChatMsg>)
                        jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);
                if(receiveWsBean.getData().getChatType()==1){
                    //群聊
                    for (Map.Entry<String, LiveRoomListener> entry : mLiveRoomListener.entrySet()) {
                        String key = entry.getKey();
                        LiveRoomListener listener = entry.getValue();
                        listener.onRoomReceive(receiveWsBean.getData());
                    }
                }else {
                    //单聊
                    for (Map.Entry<String, C2CListener> entry : mC2CListener.entrySet()) {
                        String key = entry.getKey();
                        C2CListener listener = entry.getValue();
                        listener.onC2CReceive(receiveWsBean.getData());
                    }

                }
                break;
            case 20 :
                //历史消息 http方式
                /* val s = wsBean.data as String
                 val string2map = string2map<ChatMsgBean>(s)
                 string2map?.keys
                 string2map?.values*/
            break;
            case 23 :
                //消息已读
            for (Map.Entry<String, ReadListener> entry : mReadListener.entrySet()) {
                String key = entry.getKey();
                ReadListener listener = entry.getValue();
                listener.onReadReceive(wsBean);
            }
            break;
            case 25 :
                //服务器主动推送直播间开播
               ReceiveWsBean<LiveStatus> wsBean2 = (ReceiveWsBean<LiveStatus>)
                    jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);
                for (Map.Entry<String, LiveStatusListener> entry : mLiveStatusListener.entrySet()) {
                    String key = entry.getKey();
                    LiveStatusListener listener = entry.getValue();
                    listener.onOpenLive(wsBean2.getData());
                }
                break;
            case 26 :
                //服务器主动推送直播间关播
            ReceiveWsBean<LiveStatus> wsBean3 = (ReceiveWsBean<LiveStatus>)
                    jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);
                for (Map.Entry<String, LiveStatusListener> entry : mLiveStatusListener.entrySet()) {
                    String key = entry.getKey();
                    LiveStatusListener listener = entry.getValue();
                    listener.onCloseLive(wsBean3.getData());
                }
                break;
            case 27 :
                //服务器主动推送直播间直播地址修改
                ReceiveWsBean<LiveStatus> wsBean4 = (ReceiveWsBean<LiveStatus>)
                        jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);

            for (Map.Entry<String, LiveStatusListener> entry : mLiveStatusListener.entrySet()) {
                String key = entry.getKey();
                LiveStatusListener listener = entry.getValue();
                listener.onChangeLive(wsBean4.getData());
            }
                break;
            case 28 :
            case 32 :
            case 33 :
            ReceiveWsBean<FeedSystemNoticeBean> wsBean5 = (ReceiveWsBean<FeedSystemNoticeBean>)
                    jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);

                for (Map.Entry<String, C2CListener> entry : mC2CListener.entrySet()) {
                    String key = entry.getKey();
                    C2CListener listener = entry.getValue();
                    listener.onSystemMsgReceive(wsBean5.getData());
                }
                break;
            case 29:
                //未读消息推送
                //接收消息
            ReceiveWsBean<NoReadMsg> wsBean6 = (ReceiveWsBean<NoReadMsg>)
                    jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);

                for (Map.Entry<String, NoReadMsgPushListener> entry : mNoReadMsgListener.entrySet()) {
                    String key = entry.getKey();
                    NoReadMsgPushListener listener = entry.getValue();
                    listener.onNoReadMsgNums(wsBean6.getData().getTotalCount()+"");
                }
                break;
            case 30 :
                //服务器主动推送直播间直播地址修改

               List<ReceiveChangeMsg>   mesg= jsonConvert.jsonToList(wsBean.getData().toString(), ReceiveChangeMsg.class);
                ArrayList<ReceiveChangeMsg> list=new ArrayList<>();
                list.addAll(mesg);

                for (Map.Entry<String, C2CListener> entry : mC2CListener.entrySet()) {
                    String key = entry.getKey();
                    C2CListener listener = entry.getValue();
                    listener.onChangeReceive(list);
                }

                 for (Map.Entry<String, LiveStatusListener> entry : mLiveStatusListener.entrySet()) {
                String key = entry.getKey();
                LiveStatusListener listener = entry.getValue();
                listener.onChangeReceive(list);
                  }
                  for (Map.Entry<String, OtherPushListener> entry : mOtherPushListener.entrySet()) {
                String key = entry.getKey();
                OtherPushListener listener = entry.getValue();
                listener.onChangeMatchData(list);
                 }
                break;
            case 34:
            ReceiveWsBean<BeingLiveBean> wsBean8 = (ReceiveWsBean<BeingLiveBean>)
                    jsonConvert.jsonToObject2(msg, ReceiveWsBean.class);

            for (Map.Entry<String, OtherPushListener> entry : mOtherPushListener.entrySet()) {
                String key = entry.getKey();
                OtherPushListener listener = entry.getValue();
                listener.onAnchorStartLevel(wsBean8.getData());
            }
                break;
            default:
                // 登录过期

                break;

        }


    }

    /**
     * 退出app 关闭长连接
     */
    void stopService() {
        try {
            mContext.unregisterReceiver(receiver);
            cancelAll();
            closeHeartBeat();
            closeConnect();
            client = null;
            INSTANCE = null;
        } catch (Exception e ) {
            Log.i("客户端====","stopService: ==="+e.getMessage());
        }
    }


    void cancelAll() {
        if (scheduledExecutorService != null) {
            try {
                shutdownAndAwaitTermination(scheduledExecutorService);
            } catch (Exception e) {
            }
            scheduledExecutorService = null;
        }
    }

    void shutdownAndAwaitTermination(ScheduledExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(6, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 关闭心跳
     */
    private void closeHeartBeat() {
        mHandler.removeCallbacks(heartBeatRunnable);
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }
}
