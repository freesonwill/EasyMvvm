package com.cn.game.sdk2.manager

import com.cn.game.sdk2.manager.listener.IGameListener

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 10:22
 **/
interface IGameManager {

    //开始倒计时
    fun startCountDownTimer(countdownTime:Long,countDownInterval:Long=1000L,lis: IGameListener? = null)

}