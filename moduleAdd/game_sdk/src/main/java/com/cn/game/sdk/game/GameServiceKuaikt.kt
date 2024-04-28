package com.cn.game.sdk.game

import com.cn.game.sdk.common.GameReqCode
import com.cn.game.sdk.websocket.JWebSocketClient
import com.xcjh.base_lib.utils.loge
import game.common.proto.ClientReq
import game.common.proto.ClientReq.PingBackReq
import game.mod.proc.yf.proto.req.GameReq

class GameServiceKuaikt(client: JWebSocketClient) :
    GameService {

    private var _client: JWebSocketClient? = null

    init {
        this._client = client
    }
    //发送登录消息
    override fun login(req: ClientReq.LoginReq ) {
        val mid: Short = 7
        val sid: Short = 7
        send(mid,sid, req.toByteArray())
    }

    //进入直播间
    override fun enterGroup(req : GameReq.EnterGroup) {
        send(500,GameReqCode.C2S_ENTER_GROUP.toShort(), req.toByteArray())
    }

    //离开直播间
    override fun leavelGroup() {
        send(500,GameReqCode.C2S_LEAVE_GROUP.toShort(), ByteArray(0))
    }

    //进入游戏
    override fun enterGame(req: GameReq.EnterMiniGame) {
        send(500,GameReqCode.C2S_ENTER_MINI_GAME.toShort(), req.toByteArray())
    }

    //离开游戏
    override fun leavelGame(req: GameReq.LeaveMiniGamesReq) {
        send(500,GameReqCode.C2S_LEAVE_MINI_GAME.toShort(), req.toByteArray())
    }

    //下注
    override fun bet(req : GameReq.BetReq) {
        send(500,GameReqCode.C2S_BET.toShort(), req.toByteArray())
    }

    //刷新金币
    override fun refreshScore() {
        send(500,GameReqCode.C2S_REFRESH_SCORE.toShort(), ByteArray(0))
    }
    //心跳
    override fun ping(req: PingBackReq){
        send(0,2, req.toByteArray())
    }

    fun send(mid:Short, sid:Short, data:ByteArray){
        try{
            val msg = _client!!.newPack(mid, sid , data, data.size)
            _client!!.send(msg)
        }catch (e: Exception){
            "======GameMessageKuaikt===调用异常------------  ${e.message}".loge()
        }

    }
}