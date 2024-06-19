package com.cn.game.sdk2.manager

import android.os.CountDownTimer
import android.util.Log
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.listener.IGameListener
import com.cn.game.sdk2.utils.Ext.isMainThread
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.random.Random

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
            if (oldValue != value) {
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
                    Log.d(TAG, "startCountDownTimer onTick run $isMainThread,$millisUntilFinished")
                    lis?.onCountdown(millisUntilFinished)
                    mGameListener.forEach { it.value.onCountdown(millisUntilFinished) }
                }

                override fun onFinish() {
                    Log.d(TAG,"startCountDownTimer onFinish run $isMainThread")
                    when (gameState) {
                        GameState.Betting -> gameState = GameState.Settling
                        GameState.Settling -> {
                            startDrawing()
                        }
                        else -> throw IllegalStateException("error game state:${gameState}")
                    }
                    lis?.onCountDownFinish(gameState)
                    mGameListener.forEach { it.value.onCountDownFinish(gameState) }
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
    }

    fun startSettling() {
        gameState = GameState.Settling
    }

    fun startDrawing() {
        runBlocking {
            gameState = GameState.Drawing
            val bean = HistoryResultBean(
                isShow = false,
                result = listOf(
                    Random.nextInt(1, 7),
                    Random.nextInt(1, 7),
                    Random.nextInt(1, 7)
                )
            )
            mGameListener.forEach { it.value.onDrawingResult(bean) }
            delay(200)
            gameState = GameState.DrawFinish
        }
    }

    fun setLiveStatusListener(tag: String, listener: IGameListener) {
        mGameListener[tag] = listener
    }

    fun removeLiveStatusListener(tag: String) {
        mGameListener.remove(tag)
    }
}
