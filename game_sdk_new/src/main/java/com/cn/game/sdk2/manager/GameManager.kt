package com.cn.game.sdk2.manager

import android.os.CountDownTimer
import com.cn.game.sdk2.manager.listener.IGameListener

/**
 * Description: 游戏管理类
 * author       : zhangsan
 * createTime   : 2024/6/17 10:21
 **/
class GameManager private constructor() : IGameManager {
    private var countDownTimer: CountDownTimer? = null

    companion object {
        val instance: GameManager by lazy { GameManager() }
        private const val TAG = "GameManager"
    }

    //================================ Method ===================================================//

    override fun startCountDownTimer(countdownTime: Long, countDownInterval: Long, lis: IGameListener?) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(countdownTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                lis?.onCountdown(millisUntilFinished)
            }

            override fun onFinish() {
                lis?.onCountdown(0)
            }
        }
        countDownTimer?.start()
    }

    fun stopCountDown() {
        countDownTimer?.cancel()
    }

}
