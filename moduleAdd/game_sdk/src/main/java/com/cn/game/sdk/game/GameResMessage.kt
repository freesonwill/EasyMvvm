package com.cn.game.sdk.game

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
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

    public fun register(lifecycleOwner: LifecycleOwner,sid:Int, action: (t: GameEmit) -> Unit){
        FlowBus.with<GameEmit>("$_prv_key.$sid").register(lifecycleOwner){
            action(it)
        }
    }



    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}