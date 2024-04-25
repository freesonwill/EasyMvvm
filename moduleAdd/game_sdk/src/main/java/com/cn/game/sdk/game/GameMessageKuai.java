package com.cn.game.sdk.game;


import android.util.Log;

import com.cn.game.sdk.websocket.JWebSocketClient;

import java.nio.ByteBuffer;

import game.common.proto.ClientReq;

public class GameMessageKuai{

    private String TAG = "app-GameMessage";
    private JWebSocketClient _client;
    public GameMessageKuai(JWebSocketClient client){
        _client = client;
    }

    public void onMessage(ByteBuffer bytes){
        Object[] resps = _client.newUnpack(bytes.array());
        Integer mid = (Integer) resps[0];
        Integer sid = (Integer) resps[1];
        byte[] str = (byte[]) resps[2];
        System.out.printf("mid=%d,sid=%d,str=[%s]\n", mid,sid,str);
    }

    //发送登录消息
    public void login(String agentName, String version, int server, String nikeName, String token){
        ClientReq.LoginReq req =  ClientReq.LoginReq.newBuilder()
                .setAgentName(agentName)
                .setVersion(version)
                .setPlatform(6)
                .setRequestId(8)
                .setServer(server)
                .setNickname(nikeName)
                .setToken(token)
                .build();
        short mid = 7;
        short sid = 7;
        Log.v(TAG, "mid:"+mid+" sid:"+sid+ "dataSize:"+req);

        final byte[] msg = _client.newPack(mid, sid, req.toByteArray(), req.toByteArray().length);
        _client.send(msg);
    }

    //进入直播间
    public void enterGroup(){

    }
    //离开直播间
    public void leavelGroup(){

    }
    //进入游戏
    public void enterGame(){

    }
    //离开游戏
    public void leavelGame(){

    }
    //下注
    public void bet(){

    }
    //刷新金币
    public void refreshScore(){

    }

}
