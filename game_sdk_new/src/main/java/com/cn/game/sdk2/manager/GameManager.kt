package com.cn.game.sdk2.manager

import android.os.CountDownTimer
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

    /**倒计时设置时间戳**/
    private var _countDownSetTimeStamp:Long = 0L
    /**倒计时的时间是毫秒1000*/
    var countDown:Long = -1
        get(){
            if(field == -1L) throw IllegalStateException("countDown not set")
            val elapsed = System.currentTimeMillis() -_countDownSetTimeStamp
            return field - elapsed
        }
        set(value) {
            field = value
            _countDownSetTimeStamp = System.currentTimeMillis()
        }

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
        countdownTime: Long,
        countDownInterval: Long,
        lis: IGameListener?
    ) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(countdownTime, countDownInterval) {
                override fun onTick(millisUntilFinished: Long) {
//                    Log.d(TAG, "startCountDownTimer onTick run $isMainThread,$millisUntilFinished")
                    lis?.onCountdown(millisUntilFinished)
                    mGameListener.forEach { it.value.onCountdown(millisUntilFinished) }
                }

                override fun onFinish() {
//                    Log.d(TAG,"startCountDownTimer onFinish run $isMainThread")
//                    when (gameState) {
//                        GameState.Betting -> gameState = GameState.Settling
//                        GameState.Settling -> {
//                            startDrawing()
//                        }
//                        else -> throw IllegalStateException("error game state:${gameState}")
//                    }
//                    lis?.onCountDownFinish(gameState)
//                    mGameListener.forEach { it.value.onCountDownFinish(gameState) }
//                    countDownTimer = null
                }
            }
        countDownTimer?.start()
    }
    fun stopCountDown(){
        countDownTimer?.cancel()
    }

    override val isClickOperation: Boolean
        get() {
            return this.gameState == GameState.Betting
        }

    fun startBetting() {
        gameState = GameState.Betting
    }
    fun reset(){
        gameState = GameState.Init
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
