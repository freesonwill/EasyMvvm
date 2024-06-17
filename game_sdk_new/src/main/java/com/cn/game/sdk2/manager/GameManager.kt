package com.cn.game.sdk2.manager

import android.os.CountDownTimer
import android.util.Log
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.listener.GameTimeStatic
import com.cn.game.sdk2.manager.listener.IGameCountDownListener
import com.cn.game.sdk2.utils.MyGameManager
import com.xcjh.base_lib.utils.LogUtils

/**
 * Description: 游戏管理类
 * author       : zhangsan
 * createTime   : 2024/6/17 10:21
 **/
class GameManager private constructor() : IGameManager {
    private var countDownTimer: CountDownTimer? = null

    /**
     * 倒计时的时间是毫秒1000
     */
    var countdownTime: Int = 10000

    companion object {
        val instance: GameManager by lazy { GameManager() }
        private const val TAG = "GameManager"
    }

    //================================ Method ===================================================//
    override var gameState: GameState = GameState.Init

    override fun startCountDownTimer(lis: IGameCountDownListener?) {
        countDownTimer?.cancel()
        if (countDownTimer == null) {
            countDownTimer = object : CountDownTimer(countdownTime.toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    lis?.onCountdown(millisUntilFinished)
                }

                override fun onFinish() {
                    gameState = if (gameState == GameState.Betting) {
                        GameState.Settling
                    } else {
                        GameState.Betting
                    }
                    lis?.onFinish(gameState)
                    countDownTimer = null
                }
            }
        }
        countDownTimer?.start()
    }

    override val isClickOperation: Boolean
        get() {
            return this.gameState == GameState.Betting
        }
}