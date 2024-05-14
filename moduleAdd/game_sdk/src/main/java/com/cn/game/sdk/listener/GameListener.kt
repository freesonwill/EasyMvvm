package com.cn.game.sdk.listener


interface GameTimeStatic{
    /**
     * 返回倒计时
     */
    fun  onCountdown(time:Long){}

    /**
     * 返回倒计时
     */
    fun  onStatic(mStatic:Int){}

}
