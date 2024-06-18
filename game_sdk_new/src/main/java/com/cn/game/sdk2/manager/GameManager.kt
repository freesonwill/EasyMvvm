package com.cn.game.sdk2.manager

import android.os.CountDownTimer
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.listener.IGameListener

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

    var gameState: GameState = GameState.Init
        set(value) {
            val oldValue = field
            field = value
            if(oldValue != value){
                mGameListener.forEach { it.value.onGameStateChanged(oldValue, value) }
            }
        }
    private val mGameListener = linkedMapOf<String, IGameListener>()
    //================================ Method ===================================================//

    override fun startCountDownTimer(
        countdownTime: Int,
        countDownInterval: Int,
        lis: IGameListener?
    ) {
        countDownTimer?.cancel()
        countDownTimer =
            object : CountDownTimer(countdownTime.toLong(), countDownInterval.toLong()) {
                override fun onTick(millisUntilFinished: Long) {
                    lis?.onCountdown(millisUntilFinished)
                    mGameListener.forEach {
                        it.value.onCountdown(millisUntilFinished)
                    }
                }

                override fun onFinish() {
                    when (gameState) {
                        GameState.Betting -> gameState = GameState.Settling
                        GameState.Settling -> gameState = GameState.Drawing
                        GameState.Drawing -> gameState = GameState.Betting
                        else -> throw IllegalStateException("error game state:${gameState}")
                    }
                    lis?.onFinish(gameState)
                    mGameListener.forEach { it.value.onFinish(gameState) }
                    countDownTimer = null
                }
            }
        countDownTimer?.start()
    }

    override val isClickOperation: Boolean
        get() {
            return this.gameState == GameState.Betting
        }

    fun startBetting() {
        gameState = GameState.Betting
        startCountDownTimer(10_000, 1_000)
    }

    fun startSettling() {
        gameState = GameState.Settling
        startCountDownTimer(10_000, 1_000)
    }

    fun startDrawing() {
        gameState = GameState.Drawing
        startCountDownTimer(10_000, 1_000)
    }

    fun setLiveStatusListener(tag: String, listener: IGameListener) {
        mGameListener[tag] = listener
    }

    fun removeLiveStatusListener(tag: String) {
        mGameListener.remove(tag)
    }
}
