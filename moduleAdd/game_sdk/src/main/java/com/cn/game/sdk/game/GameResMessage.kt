package com.cn.game.sdk.game

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.cn.game.sdk.common.GameResCode
import game.mod.proc.yf.proto.res.GameRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object GameResMessage  : LifecycleOwner {
    var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    var _prv_key = "message_key"

    init {
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED)
    }
    public fun onMessage(mid:Int, sid:Int, byteArray: ByteArray){
        GlobalScope.launch {
            FlowBus.with<GameEmit>("$_prv_key.$sid").post(GameEmit(mid, sid, byteArray))
        }
    }

    //获取房间信息
    public fun enterInfo(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.EnterInfo) -> Unit){
        FlowBus.with<GameRes.EnterInfo>("$_prv_key.${GameResCode.S2C_ENTER_INFO}").register(lifecycleOwner){
            action(it)
        }
    }
    //获取直播间信息
    public fun groupInfo(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.GroupInfo) -> Unit){
        FlowBus.with<GameRes.GroupInfo>("$_prv_key.${GameResCode.S2C_GROUP_INFO}").register(lifecycleOwner){
            action(it)
        }
    }
    //获取小游戏信息
    public fun enterMiniGameInfo(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.EnterMiniGameInfo) -> Unit){
        FlowBus.with<GameRes.EnterMiniGameInfo>("$_prv_key.${GameResCode.S2C_ENTER_MINI_GAME_INFO}").register(lifecycleOwner){
            action(it)
        }
    }

    //开始新局
    public fun beginRound(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.BeginNewRound) -> Unit){
        FlowBus.with<GameRes.BeginNewRound>("$_prv_key.${GameResCode.S2C_BEGIN_ROUND}").register(lifecycleOwner){
            action(it)
        }
    }

    //开始结算
    public fun beginSettle(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.BeginSettle) -> Unit){
        FlowBus.with<GameRes.BeginSettle>("$_prv_key.${GameResCode.S2C_BEGIN_SETTLE}").register(lifecycleOwner){
            action(it)
        }
    }

    //玩家下注结果
    public fun miniGameBetResult(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.MyMiniGameBetResult) -> Unit){
        FlowBus.with<GameRes.MyMiniGameBetResult>("$_prv_key.${GameResCode.S2C_USER_BET_RESULT}").register(lifecycleOwner){
            action(it)
        }
    }

    //同步注區下注信息
    public fun syncAreaBetInfo(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.SyncAreaBetInfo) -> Unit){
        FlowBus.with<GameRes.SyncAreaBetInfo>("$_prv_key.${GameResCode.S2C_SYNC_AREA_BET_INFO}").register(lifecycleOwner){
            action(it)
        }
    }

    //通知離開group
    public fun leaveGroup(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.LeaveGroup) -> Unit){
        FlowBus.with<GameRes.LeaveGroup>("$_prv_key.${GameResCode.S2C_LEAVE_GROUP}").register(lifecycleOwner){
            action(it)
        }
    }
    //通知离开小游戏
    public fun leaveMiniGames(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.LeaveMiniGames) -> Unit){
        FlowBus.with<GameRes.LeaveMiniGames>("$_prv_key.${GameResCode.S2C_LEAVE_MINI_GAME}").register(lifecycleOwner){
            action(it)
        }
    }

    //刷新屬性
    public fun refreshUserProperties(lifecycleOwner: LifecycleOwner ,action: (t: GameRes.RefreshUserProperties) -> Unit){
        FlowBus.with<GameRes.RefreshUserProperties>("$_prv_key.${GameResCode.S2C_REFRESH_USER_PROPS}").register(lifecycleOwner){
            action(it)
        }
    }

    public fun register(lifecycleOwner: LifecycleOwner,sid:Int, action: (t: GameEmit) -> Unit){
        FlowBus.with<GameEmit>("$_prv_key.$sid").register(lifecycleOwner){
            action(it)
        }
    }



    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}