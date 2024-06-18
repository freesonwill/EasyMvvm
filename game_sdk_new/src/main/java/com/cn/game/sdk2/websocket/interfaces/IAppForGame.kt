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