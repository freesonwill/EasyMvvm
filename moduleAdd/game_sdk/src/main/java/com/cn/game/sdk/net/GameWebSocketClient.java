package com.cn.game.sdk.net;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class GameWebSocketClient extends WebSocketClient {

    private String TAG = "GameWebSocketClient";
    public long mNativePtr = 0;
    public GameWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());

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
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e(TAG, reason);
    }

    @Override
    public void onError(Exception ex) {
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
    public native Object[] unpack(byte[] data);
    public native long nativeCreateChiper();
    public native void nativeFinalizer(long ptr);
}
