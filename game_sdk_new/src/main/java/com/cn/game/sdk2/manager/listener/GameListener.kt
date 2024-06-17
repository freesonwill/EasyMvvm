package com.cn.game.sdk2.manager.listener


interface GameTimeStatic{
    /**
     * 返回倒计时
     */
    fun  onCountdown(time:Long){}

    /**
     * 结算结束
     */
    fun  onStatic(mStatic:Int){}

}
