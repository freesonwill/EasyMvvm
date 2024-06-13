package com.cn.game.sdk.game

import android.view.View
import game.mod.proc.yf.proto.req.GameReq.EnterMiniGame
import game.mod.proc.yf.proto.res.GameRes
import game.mod.proc.yf.proto.res.GameRes.GroupInfo

class GameData private constructor()  {
    companion object {
        private val instance: GameData by lazy {
            GameData()
        }

        @JvmName("getInstance1")
        fun getInstance(): GameData {
            @Suppress("UNCHECKED_CAST")
            return instance as GameData
        }
    }
    private var enterInfo = GameRes.EnterInfo.newBuilder().build()
    private var groupInfo = GroupInfo.newBuilder().build()
    private var miniGameInfo = GameRes.EnterMiniGameInfo.newBuilder().build()

    public var rootView:View? = null

    fun setEnterInfo(res:GameRes.EnterInfo) {
        enterInfo = res
    }

    fun getEnterInfo(): GameRes.EnterInfo{
        return enterInfo
    }

    fun setGroupInfo(res:GroupInfo){
        groupInfo = res
    }

    fun getGroupInfo(): GroupInfo{
        return groupInfo
    }

    fun setGameInfo(res:GameRes.EnterMiniGameInfo){
        miniGameInfo = res
    }

    fun getGameInfo(): GameRes.EnterMiniGameInfo {
        return miniGameInfo
    }


}