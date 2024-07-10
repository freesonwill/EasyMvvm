package com.cn.game.sdk2.manager.listener

import androidx.annotation.UiThread
import com.cn.game.sdk2.data.bean.HistoryResultBean

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 11:33
 **/
interface IGameListener {

    /**
     * 返回倒计时
     */
    @UiThread fun onCountdown(time: Long){}

}