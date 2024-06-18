package com.cn.game.sdk2.websocket

import android.annotation.SuppressLint
import android.view.View
import com.cn.game.sdk2.websocket.interfaces.IAppForGame

/**
 * socket-url
 */
var WEB_SOCKET_URL = "wss://ws.qxe68.com:7001/api/game/5702" ///test

/**
 * 用户余额
 */
var balance: Int = 0

/**
 * 是否能下注
 * 判断依据：
 *  - 游戏状态
 *  - 余额是否足够
 */
var isCanBetting: Boolean = true
    get() {
        return true
    }

/**
 * 上一次的下注结果
 */
var previousSuccess: Boolean = true

/**
 * UI将左上角结果视图赋值给该变量
 * 返回给app使用
 */
@SuppressLint("StaticFieldLeak")
var resultView: View? = null

/**
 * 同理 @see resultView
 * 返回给app使用
 */
@SuppressLint("StaticFieldLeak")
var floatingView: View? = null

/**
 * 主播可以设置直播间是否允许下注
 * 默认 true
 */
var isAllowedBet = true

/**
 * 是否显示游戏
 */
var isShowGame = true

/**
 * app实现的接口
 * 用于通知app ：
 *  - 历史记录按钮被点击
 *  - 客服按钮被点击
 *  - token失效
 */
var appListener: IAppForGame? = null

/**
 *
 */
var gameMassageManager = GameSocketManager.getInstance()?.getGameService()

fun <T> List<T>.isNotEmpty(block: (List<T>) -> Unit): Boolean {
    if (this.isNotEmpty()) {
        block(this)
        return true
    }
    return false
}

fun <K, V> Map<K, V>.isNotEmpty(block: (Map<K, V>) -> Unit): Boolean {
    if (this.isNotEmpty()) {
        block(this)
        return true
    }
    return false
}

fun Boolean.isEmpty(block: () -> Unit) {
    if (!this) {
        block()
    }
}