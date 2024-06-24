package com.cn.game.sdk2.websocket.interfaces

/**
 * app需要实现此接口
 * 用于sdk调用
 */
interface IAppForGame {

    fun historyOfBetAction()

    fun customerServiceAction()

    fun getTokenLoseEffectiveness()
}

interface SDKLoginCallbackListener {
    fun callback(code: Int, message: String? = null)
}

interface SDKEnterLiveCallbackListener {
    fun callback(code: Int, message: String? = null)
}
interface SDKLeaveLiveCallbackListener {
    fun callback(code: Int, message: String? = null)
}
interface SDKCancelGameCallbackListener {
    fun callback(code: Int, message: String? = null)
}
