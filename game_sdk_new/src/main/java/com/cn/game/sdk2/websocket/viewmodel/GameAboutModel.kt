package com.cn.game.sdk2.websocket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import org.junit.experimental.max.MaxHistory

class GameAboutModel : ViewModel() {
    enum class Stage {
        NEW, DEAL, SETTLE
    }

    private val _currentStage = MutableLiveData<Stage>()
    private val _balance = MutableLiveData<Int>()
    private val _syncAreaBetInfo = MutableLiveData<List<AreaBetBean>>()
    private val _clearTrendsIds = MutableLiveData<List<Int>>()
    private val _historyRounds = MutableLiveData<List<RoundInfoBean>>()

    /**
     * 实现currentStage的observe，监听阶段变化
     */
    val currentStage: LiveData<Stage>
        get() = _currentStage

    /**
     * 实现balance的observe，监听余额变化
     * 该值需要缩小100倍，保留两位小数用于展示
     */
    val balance: LiveData<Int>
        get() = _balance

    /**
     * 实现syncAreaBetInfo的observe，监听default牌面的人数变化
     */
    val syncAreaBetInfo: LiveData<List<AreaBetBean>>
        get() = _syncAreaBetInfo

    val clearTrendsIds: LiveData<List<Int>>
        get() = _clearTrendsIds

    val historyRounds:LiveData<List<RoundInfoBean>>
        get() = _historyRounds

    fun changeBalance(b: Int) {
        _balance.value = b
    }

    fun changeStage(stage: Stage) {
        _currentStage.value = stage
    }

    fun changeAreaBetInfo(info:List<AreaBetBean>){
        _syncAreaBetInfo.value = info
    }

    fun clearTrends(ids:List<Int>){
        _clearTrendsIds.value = ids
    }

    fun addHistoryRounds(history: List<RoundInfoBean> ){
        _historyRounds.value = history
    }

    fun addHistoryRound(item:RoundInfoBean){
        val history = _historyRounds.value ?: listOf()
        _historyRounds.value = history + item
    }

    var miniGameId: Int = 0
    var countDown: Int = 0 //阶段倒计时
    var roundId: String = "" //期号

    /**
     * 结算阶段使用
     * 开奖注区，用于展示注区的闪闪动画
     */
    var lotteryResultList: ArrayList<Betting>? = null //开奖注区

    /**
     * 结算阶段使用
     * 净收入，用于展示中奖动画；使用时需要缩小100倍
     */
    var netIncome: Int = 0

    /**
     * 结算阶段使用
     * 用户中间后的面板砝码金额已经中奖注区
     */
    var userLotteryResult: ArrayList<BettingRecordBean>? = null
}