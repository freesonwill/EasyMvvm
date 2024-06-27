package com.cn.game.sdk2.manager

import android.os.CountDownTimer
import android.util.Log
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.listener.IGameListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

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

    var gameState: GameState = GameState.Init
        set(value) {
            val oldValue = field
            field = value
            if (oldValue != value) {
                mGameListener.forEach { it.value.onGameStateChanged(oldValue, value) }
            }
        }
    private val mGameListener = linkedMapOf<String, IGameListener>()
    //================================ Method ===================================================//

    override fun startCountDownTimer(countdownTime: Long, countDownInterval: Long, lis: IGameListener?) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(countdownTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                lis?.onCountdown(millisUntilFinished)
                mGameListener.forEach { it.value.onCountdown(millisUntilFinished) }
            }

            override fun onFinish() {

            }
        }
        countDownTimer?.start()
    }

    fun stopCountDown() {
        countDownTimer?.cancel()
    }

    override val isClickOperation: Boolean
        get() {
            return this.gameState == GameState.Betting
        }

    fun reset() {
        gameState = GameState.Init
    }


    fun setLiveStatusListener(tag: String, listener: IGameListener) {
        mGameListener[tag] = listener
    }

    fun removeLiveStatusListener(tag: String) {
        mGameListener.remove(tag)
    }
}
