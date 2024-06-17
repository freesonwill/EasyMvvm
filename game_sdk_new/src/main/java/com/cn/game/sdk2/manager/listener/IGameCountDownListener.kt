package com.cn.game.sdk2.manager.listener

import com.cn.game.sdk2.data.enums.GameState

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 11:33
 **/
interface IGameCountDownListener {

    /**
     * 返回倒计时
     */
    fun  onCountdown(time:Long)

    /**
     * 结算结束
     */
    fun  onFinish(state:GameState)
}