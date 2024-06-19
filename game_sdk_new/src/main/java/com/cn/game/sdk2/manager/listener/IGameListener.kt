package com.cn.game.sdk2.manager.listener

import androidx.annotation.UiThread
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.enums.GameState

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 11:33
 **/
interface IGameListener {

    /**
     * 返回倒计时
     */
    @UiThread fun onCountdown(time: Long)

    /**
     * 计时结束
     */
    @UiThread fun onCountDownFinish(state: GameState)

    /**
     *游戏状态变化
     */
    @UiThread fun onGameStateChanged(oldValue: GameState, newValue: GameState)

    /**
     * 开奖结果
     */
    @UiThread fun onDrawingResult(result: HistoryResultBean)
}