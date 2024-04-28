
package com.cn.game.sdk.websocket;

import android.util.Log;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLParameters;

import game.common.proto.ClientReq;

public class JWebSocketClient extends WebSocketClient {
    static {
        System.loadLibrary("util");
    }

    private String TAG = "JWebSocketClient";
    private MyWsManager _wsMngr;
    public long mNativePtr = 0; // ccpayloadchiper

    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
//        super.onSetSSLParameters(sslParameters);
    }

    public JWebSocketClient(MyWsManager wsMngr, URI serverUri) {
        super(serverUri, new Draft_6455());
        _wsMngr = wsMngr;
        createChiper();
    }

    protected void finalize() {
        try {
            nativeFinalizer(mNativePtr);
        } finally {
            try {
                super.finalize();
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {//在webSocket连接开启时调用
        //this.testLogin();
        //login("wali-internal","6", 8, "android","14%3AhRyvRPoB");
        ClientReq.LoginReq req = ClientReq.LoginReq.newBuilder()
                .setAgentName("wali-internal")
                .setServer(8)
                .setToken("20:exo71eVM")
                .setRequestId(6)
                .setVersion("6")
                .setNickname("android")
                .build();
        _wsMngr.gameMessage().login(req);
    }

    @Override
    public void onMessage(String message) {//接收到消息时调用
        //message就是接收到的消息
        Log.d(TAG, "onMessage text:" + message);
    }

    public void onMessage(ByteBuffer bytes) {
        if (!bytes.hasRemaining()) {
            return;
        }
        Object[] resps = newUnpack(bytes.array());
        Integer mid = (Integer) resps[0];
        Integer sid = (Integer) resps[1];
        byte[] str = bytes.array();
        if(resps.length >2){
            str = (byte[]) resps[2];
        }
        _wsMngr.onMessage(mid, sid, str);
//        if (sid.equals(107)){
////            try {
////                ClientRes.ErrorMessage errorMsg = ClientRes.ErrorMessage.parseFrom(str);
////                Log.d(TAG, errorMsg.getDesc());
////            } catch (InvalidProtocolBufferException e) {
////                e.printStackTrace();
////            }
//        }
//        Log.d(TAG, ">>>>>unpack");
//        System.out.printf("mid=%d,sid=%d,str=[%s]\n", mid,sid,str);
//        Log.d(TAG, "mid="+mid+",sid="+sid+",str="+str);
//        Log.d(TAG, "<<<<<unpack");
    }

    public void sendMessage(int sid, byte[] req){
        short mid = 500;
        if( sid == 7){
            mid = 7;
        }
        final byte[] msg = newPack(mid, (short) sid, req, req.length);
        send(msg);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {//在连接断开时调用
        Log.e(TAG, reason);
    }

    @Override
    public void onError(Exception ex) {//在连接出错时调用
        Log.e(TAG, ex.toString());
    }

    void createChiper()
    {
        mNativePtr = 0;
        Log.d(TAG, "createChiper1:" + String.valueOf(mNativePtr));
        mNativePtr = nativeCreateChiper();
        Log.d(TAG, "createChiper2:" + String.valueOf(mNativePtr));
    }

    public native byte[] pack(short mid, short sid, String data, int dataSize);
    public native byte[] newPack(short mid, short sid, byte[] data, int dataSize);
    public native Object[] unpack(byte[] data);
    public native Object[] newUnpack(byte[] data);
    public native long nativeCreateChiper();
    public native void nativeFinalizer(long ptr);
}
