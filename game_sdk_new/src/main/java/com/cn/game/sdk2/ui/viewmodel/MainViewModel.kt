package com.cn.game.sdk2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.enums.GameEnum
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.base.BaseViewModel
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.getColor

/**
 * 負責main的資料處理
 */
class MainViewModel : BaseViewModel() {

    private val _game = MutableLiveData(GameEnum.GAME_FAST3)
    val game: LiveData<GameEnum> = _game

    val homeTimeSeconds: LiveData<Int> = gameAboutModel.countDownSecondsLD
    val homeTimeColorLD: LiveData<Int> by lazy {
        Transformations.map(this.homeTimeSeconds) {
            if (it <= 5) return@map getColor(R.color.c_F34D41)
            if (it <= 10) return@map getColor(R.color.c_FFCB15)
            return@map getColor(R.color.c_62DF57)
        }
    }

    //游戏状态
    internal val gameState: GameStage? get() = gameAboutModel.currentStage.value
    internal var localGameStage: GameStage? = null

    /**
     * 余额
     */
    var currentMoney: Long = gameAboutModel.balance.value ?: 0L

    val countDown: Long
        get() {
            LogUtils.dTag(TAG, "countDown get ${gameAboutModel.countDown}")
            //return GameManager.instance.countDown
            return gameAboutModel.countDown.toLong()
        }
    val isCountDownStart: Boolean get() = gameAboutModel.isCountDownStart
}