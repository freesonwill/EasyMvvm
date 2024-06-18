package com.cn.game.sdk2.manager

import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.listener.IGameListener

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 10:22
 **/
interface IGameManager {

    //开始倒计时
    fun startCountDownTimer(countdownTime:Int,countDownInterval:Int=1000,lis: IGameListener? = null)

    //是否可以点击
    val isClickOperation: Boolean
}