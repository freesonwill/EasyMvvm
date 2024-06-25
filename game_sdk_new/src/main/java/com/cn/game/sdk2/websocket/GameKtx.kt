package com.cn.game.sdk2.websocket

import android.annotation.SuppressLint
import android.view.View
import com.cn.game.sdk2.websocket.bean.BOOM_1
import com.cn.game.sdk2.websocket.bean.BOOM_2
import com.cn.game.sdk2.websocket.bean.BOOM_3
import com.cn.game.sdk2.websocket.bean.BOOM_4
import com.cn.game.sdk2.websocket.bean.BOOM_5
import com.cn.game.sdk2.websocket.bean.BOOM_6
import com.cn.game.sdk2.websocket.bean.BOOM_ALL
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.DEFAULT_BIG
import com.cn.game.sdk2.websocket.bean.DEFAULT_DOUBLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SINGLE
import com.cn.game.sdk2.websocket.bean.DEFAULT_SMALL
import com.cn.game.sdk2.websocket.bean.DOUBLE_1
import com.cn.game.sdk2.websocket.bean.DOUBLE_2
import com.cn.game.sdk2.websocket.bean.DOUBLE_3
import com.cn.game.sdk2.websocket.bean.DOUBLE_4
import com.cn.game.sdk2.websocket.bean.DOUBLE_5
import com.cn.game.sdk2.websocket.bean.DOUBLE_6
import com.cn.game.sdk2.websocket.bean.SINGLE
import com.cn.game.sdk2.websocket.bean.SINGLE_1
import com.cn.game.sdk2.websocket.bean.SINGLE_2
import com.cn.game.sdk2.websocket.bean.SINGLE_3
import com.cn.game.sdk2.websocket.bean.SINGLE_4
import com.cn.game.sdk2.websocket.bean.SINGLE_5
import com.cn.game.sdk2.websocket.bean.SINGLE_6
import com.cn.game.sdk2.websocket.bean.SUM_10
import com.cn.game.sdk2.websocket.bean.SUM_11
import com.cn.game.sdk2.websocket.bean.SUM_12
import com.cn.game.sdk2.websocket.bean.SUM_13
import com.cn.game.sdk2.websocket.bean.SUM_14
import com.cn.game.sdk2.websocket.bean.SUM_15
import com.cn.game.sdk2.websocket.bean.SUM_16
import com.cn.game.sdk2.websocket.bean.SUM_17
import com.cn.game.sdk2.websocket.bean.SUM_4
import com.cn.game.sdk2.websocket.bean.SUM_5
import com.cn.game.sdk2.websocket.bean.SUM_6
import com.cn.game.sdk2.websocket.bean.SUM_7
import com.cn.game.sdk2.websocket.bean.SUM_8
import com.cn.game.sdk2.websocket.bean.SUM_9
import com.cn.game.sdk2.websocket.bean.areaMap
import com.cn.game.sdk2.websocket.imp.UIMethodImpl
import com.cn.game.sdk2.websocket.interfaces.IAppForGame
import com.cn.game.sdk2.websocket.interfaces.SDKCancelGameCallbackListener
import com.cn.game.sdk2.websocket.interfaces.SDKEnterLiveCallbackListener
import com.cn.game.sdk2.websocket.interfaces.SDKLeaveLiveCallbackListener
import com.cn.game.sdk2.websocket.interfaces.SDKLoginCallbackListener
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.cn.game.sdk2.websocket.viewmodel.MessageViewModel

/**
 * socket-url
 */
//var WEB_SOCKET_URL = "wss://ws.qxe68.com:7001/api/game/5702" ///test
var WEB_SOCKET_URL = "wss://ws.qxe68.com:7001/api/game/5702" ///test

/**
 * data层使用，view不管
 */
var balance: Int = 2000000

/**
 * 小游戏id
 */
var miniGameId: Int = 0

var gameAboutModel = GameAboutModel()

var messageViewModel: MessageViewModel? = null

/**
 * 是否能下注
 * 判断依据：
 *  - 游戏状态
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
var gameMassageManager: UIMethodImpl? = null

/**
 * app调用时的返回方法
 */
var mLoginCallback: SDKLoginCallbackListener? = null
var mEnterLiveCallback: SDKEnterLiveCallbackListener? = null
var mLeaveLiveCallback: SDKLeaveLiveCallbackListener? = null
var mCancelGameCallback: SDKCancelGameCallbackListener? = null

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

fun List<Int>.calculateArea(): ArrayList<Betting> {
    if (this.size != 3) return java.util.ArrayList()
    val num1 = this[0]
    val num2 = this[1]
    val num3 = this[2]
    val sum = num1 + num2 + num3
    val betAreaList = ArrayList<Betting>()
    //------默认------
    //大小
    val betArea1 = if (sum >= 11) {
        DEFAULT_BIG()
    } else {
        DEFAULT_SMALL()
    }
    //单双
    val betArea2 = if (sum % 2 == 0) {
        DEFAULT_DOUBLE()
    } else {
        DEFAULT_SINGLE()
    }
    betAreaList.add(betArea1)
    betAreaList.add(betArea2)

    //豹子
    if (isEquals()) {
        betAreaList.add(BOOM_ALL())
        when (num1) {
            1 -> {
                betAreaList.add(BOOM_1())
                betAreaList.add(DOUBLE_1())
            }

            2 -> {
                betAreaList.add(BOOM_2())
                betAreaList.add(DOUBLE_2())
            }

            3 -> {
                betAreaList.add(BOOM_3())
                betAreaList.add(DOUBLE_3())
            }

            4 -> {
                betAreaList.add(BOOM_4())
                betAreaList.add(DOUBLE_4())
            }

            5 -> {
                betAreaList.add(BOOM_5())
                betAreaList.add(DOUBLE_5())
            }

            6 -> {
                betAreaList.add(BOOM_6())
                betAreaList.add(DOUBLE_6())
            }
        }
    }
    //------总和------排除三个一样的 3 6 9 12 15 18
    when (sum) {
        4 -> betAreaList.add(SUM_4())
        5 -> betAreaList.add(SUM_5())
        6 -> {
            if (!isEquals()) betAreaList.add(SUM_6())
        }

        7 -> betAreaList.add(SUM_7())
        8 -> betAreaList.add(SUM_8())
        9 -> {
            if (!isEquals()) betAreaList.add(SUM_9())
        }

        10 -> betAreaList.add(SUM_10())
        11 -> betAreaList.add(SUM_11())
        12 -> {
            if (!isEquals()) betAreaList.add(SUM_12())
        }

        13 -> betAreaList.add(SUM_13())
        14 -> betAreaList.add(SUM_14())
        15 -> {
            if (!isEquals()) betAreaList.add(SUM_15())
        }

        16 -> betAreaList.add(SUM_16())
        17 -> betAreaList.add(SUM_17())
    }
    //------对子------
    isPairs { double, num ->
        if (double) {
            when (num) {
                1 -> {
                    betAreaList.add(DOUBLE_1())
                }

                2 -> {
                    betAreaList.add(DOUBLE_2())
                }

                3 -> {
                    betAreaList.add(DOUBLE_3())
                }

                4 -> {
                    betAreaList.add(DOUBLE_4())
                }

                5 -> {
                    betAreaList.add(DOUBLE_5())
                }

                6 -> {
                    betAreaList.add(DOUBLE_6())
                }
            }
        }
    }
    //----单个----
    val countMap = countSingle()
    countMap.forEach {
        when (it.key) {
            1 -> {
                betAreaList.add(SINGLE_1(count = it.value))
            }

            2 -> {
                betAreaList.add(SINGLE_2(count = it.value))
            }

            3 -> {
                betAreaList.add(SINGLE_3(count = it.value))
            }

            4 -> {
                betAreaList.add(SINGLE_4(count = it.value))
            }

            5 -> {
                betAreaList.add(SINGLE_5(count = it.value))
            }

            6 -> {
                betAreaList.add(SINGLE_6(count = it.value))
            }
        }
    }
    return betAreaList
}

fun List<Int>.isEquals(): Boolean {
    if (this.size != 3) return false
    return this[0] == this[1] && this[0] == this[2]
}

fun List<Int>.isPairs(block: (double: Boolean, num: Int) -> Unit) {
    if (this.size != 3) {
        block(false, -1)
    } else {
        val num1 = this[0]
        val num2 = this[1]
        val num3 = this[2]
        if (num1 == num2) {
            block(true, num1)
        } else if (num1 == num3) {
            block(true, num1)
        } else if (num2 == num3) {
            block(true, num2)
        } else {
            block(false, -1)
        }
    }
}

fun List<Int>.sum(): Int {
    if (this.size != 3) {
        return 0
    }
    val num1 = this[0]
    val num2 = this[1]
    val num3 = this[2]
    return num1 + num2 + num3
}

fun List<Int>.isBig(): Boolean {
    return sum() >= 11
}

fun List<Int>.isDouble(): Boolean {
    return sum() % 2 == 0
}

fun List<Int>.countSingle(): HashMap<Int, Int> {
    val countMap = HashMap<Int, Int>()
    forEach {
        if (countMap.containsKey(it)) {
            countMap[it] = countMap[it]!! + 1
        } else {
            countMap[it] = 1
        }
    }
    return countMap
}

fun List<Betting>.calculateUserLotteryResult(userBettingMap: Map<Betting, BettingRecordBean>): ArrayList<BettingRecordBean> {
    val userLotteryResult = ArrayList<BettingRecordBean>()
    forEach {
        if (userBettingMap.containsKey(it)) {
            val betting = userBettingMap[it]
            betting?.money = if (it is SINGLE) {
                (betting?.money!! * it.multipliers[it.count] * 100).toInt()
            } else {
                (betting?.money!! * it.multiplier * 100).toInt()
            }
            userLotteryResult.add(betting)
        }
    }
    return userLotteryResult
}

fun Int.convertBetting(): Betting? {
    return if (areaMap.containsKey(this)) {
        areaMap[this]
    } else {
        null
    }
}
