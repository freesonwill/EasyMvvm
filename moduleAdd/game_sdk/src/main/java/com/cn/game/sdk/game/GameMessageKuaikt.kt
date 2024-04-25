package com.cn.game.sdk.game

import android.util.Log
import com.cn.game.sdk.common.GameReqCode
import com.cn.game.sdk.websocket.JWebSocketClient
import game.common.proto.ClientReq.LoginReq
import game.mod.proc.yf.proto.req.GameReq
import game.mod.proc.yf.proto.res.GameRes.EnterInfo
import java.nio.ByteBuffer

class GameMessageKuaikt(client: JWebSocketClient) :GameMessage{

    private val TAG = "app-GameMessage"
    private var _client: JWebSocketClient? = null

    init {
        this._client = client
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
        val msg = _client!!.newPack(mid, sid, req.toByteArray(), req.toByteArray().size)
        _client!!.send(msg)

    }

    //进入直播间
    override fun enterGroup(req : GameReq.EnterGroup) {
        var sid = GameReqCode.C2S_ENTER_GROUP as Short
        val msg = _client!!.newPack(500, sid , req.toByteArray(), req.toByteArray().size)
        _client!!.send(msg)
    }

    //离开直播间
    override fun leavelGroup() {
        var sid = GameReqCode.C2S_LEAVE_GROUP as Short
        var data = ByteArray(0)
        val msg = _client!!.newPack(500, sid , data, data.size)
        _client!!.send(msg)
    }

    //进入游戏
    override fun enterGame(req: GameReq.EnterMiniGame) {
        var sid = GameReqCode.C2S_ENTER_MINI_GAME as Short
        val msg = _client!!.newPack(500, sid , req.toByteArray(), req.toByteArray().size)
        _client!!.send(msg)
    }

    //离开游戏
    override fun leavelGame(req: GameReq.LeaveMiniGamesReq) {
        var sid = GameReqCode.C2S_LEAVE_MINI_GAME as Short
        val msg = _client!!.newPack(500, sid , req.toByteArray(), req.toByteArray().size)
        _client!!.send(msg)
    }

    //下注
    override fun bet(req : GameReq.BetReq) {
        var sid = GameReqCode.C2S_BET as Short
        val msg = _client!!.newPack(500, sid , req.toByteArray(), req.toByteArray().size)
        _client!!.send(msg)
    }

    //刷新金币
    override fun refreshScore() {
        var sid = GameReqCode.C2S_REFRESH_SCORE as Short
        var data = ByteArray(0)
        val msg = _client!!.newPack(500, sid ,data, data.size)
        _client!!.send(msg)
    }
}