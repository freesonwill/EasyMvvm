package com.cn.game.sdk.game

import android.util.Log
import com.cn.game.sdk.websocket.JWebSocketClient
import game.common.proto.ClientReq.LoginReq
import java.nio.ByteBuffer

class GameMessageKuaikt(client: JWebSocketClient) :GameMessage{

    private val TAG = "app-GameMessage"
    private var _client: JWebSocketClient? = null

    init {
        this._client = client
    }

    fun onMessage(bytes: ByteBuffer) {
        val resps = _client!!.newUnpack(bytes.array())
        val mid = resps[0] as Int
        val sid = resps[1] as Int
        val str = resps[2] as ByteArray
        System.out.printf("mid=%d,sid=%d,str=[%s]\n", mid, sid, str)
    }

    //发送登录消息
    override fun login(
        agentName: String?,
        version: String?,
        server: Int,
        nikeName: String?,
        token: String?
    ) {
        val req = LoginReq.newBuilder()
            .setAgentName(agentName)
            .setVersion(version)
            .setPlatform(6)
            .setRequestId(8)
            .setServer(server)
            .setNickname(nikeName)
            .setToken(token)
            .build()
        val mid: Short = 7
        val sid: Short = 7
        Log.v(TAG, "mid:" + mid + " sid:" + sid + "dataSize:" + req)
        val msg = _client!!.newPack(mid, sid, req.toByteArray(), req.toByteArray().size)
        _client!!.send(msg)
    }

    //进入直播间
    override fun enterGroup() {}

    //离开直播间
    override fun leavelGroup() {}

    //进入游戏
    override fun enterGame() {}

    //离开游戏
    override fun leavelGame() {}

    //下注
    override fun bet() {}

    //刷新金币
    override fun refreshScore() {}
}