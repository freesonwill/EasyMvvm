package com.cn.game.sdk2.websocket.viewmodel

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.BettingResponsesBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.constants.AgainDoubleState
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.helper.CountDownHelper
import com.xcjh.base_lib2.base.BaseViewModel
import com.xcjh.base_lib2.callback.livedata.UnPeekLiveData
import game.mod.proc.yf.proto.res.GameRes
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

//internal
internal class GameAboutModel  {
    private val _currentStage = UnPeekLiveData<GameStage>()
    private val _currentAgainDoubleState = UnPeekLiveData<AgainDoubleState>()
    private val _balance = UnPeekLiveData<Long>()
    private val _tempBalance = UnPeekLiveData<Long>()
    private val _syncAreaBetInfo = UnPeekLiveData<List<AreaBetBean>>()
    private val _clearTrendsIds = UnPeekLiveData<List<Int>>()
    private val _historyRounds = UnPeekLiveData<List<RoundInfoBean>>()

    private val _isLoginSuccess = UnPeekLiveData<Boolean>()
    private val _isSitDown = UnPeekLiveData<Boolean>()
    private val _isEnterGroup = UnPeekLiveData<Boolean>()
    private val _isLeaveGroup = UnPeekLiveData<Boolean>()

    private val _isBettingSuccess = UnPeekLiveData<BettingResponsesBean>()
    private val _toastErrorMessage = UnPeekLiveData<String>()
    private val _isShowGame = UnPeekLiveData<Boolean>()
    private val _isAllowedBet = UnPeekLiveData<Boolean>()
    private val _moreGames = UnPeekLiveData<List<GameHallItem>>()
    var moreGames: LiveData<List<GameHallItem>> = _moreGames

    var isOpen: Boolean = false

    //设置人为豹子
    var manualLeopard: Boolean = false

    //是否是主播： 主播只能看到"热门"游戏分类，"热门"分类中以后只会放sdk游戏，在大厅弹窗处，主播端看不到其他的tab和瓦力游戏。
    var isAnchor: Boolean = false
    lateinit var agentName: String
    lateinit var token: String

    lateinit var liveId: String
    lateinit var gameIds: List<Int>
    lateinit var data: String

    var isEnterGameSuccess = false


    //如果不需要显示(isShowHistoryAndCustomer = false)，则主播端的更多只显示切换游戏和帮助。
    var simplifyMoreButtons: Boolean = true

    /** 需要监听的字段
     * @see currentAgainDoubleState 续压和加倍监听
     *  NUll 不显示
     *  AGAIN 续压
     *  DOUBLE 加倍
     *
     * @see currentStage : 监听阶段变化
     *  @param [NEW, DEAL, SETTLE] -> [新局开始，开奖中，结算中]
     *  @param countDown
     *  @param roundId
     *  当stage = SETTLE时，开奖结果为   @see [currentSettleResult]
     *                     开奖注区     @see [lotteryResultList]
     *                     净收入       @see [netIncome]
     *                     用户中奖注区  @see [userLotteryResult]
     *
     * @see balance : 监听余额变化,需要缩小100倍，保留两位小数用于展示
     *
     * @see syncAreaBetInfo ; 监听default牌面的人数变化
     *
     * @see historyRounds : 开奖历史记录
     *
     * @see isMeetAgain : 显示隐藏续压按钮
     *
     * @see isBettingSuccess : 下注是否成功
     *  @param bettingMessage 下注结果
     *
     * @see isLoginSuccess : 登录结果
     *  @param loginErrorMessage 登录失败才有
     *
     *  @see toastErrorMessage 弹窗信息
     *
     * 直接使用的字段
     *
     *
    > - countDown 阶段倒计时
    > - roundId 期号
    > - loginErrorMessage
    > - lotteryResultList ->返回的是注区集合：结算阶段使用，开奖注区，用于展示注区的闪闪动画
    > - netIncome 净收入，用于展示中奖动画；使用时需要缩小100倍
    > - userLotteryResult 用户中奖后的面板砝码金额已经中奖注区
    > - bettingMessage 下注失败的message
     */

    /*********开始玩之前的阶段的状态***********/
    val isLoginSuccess
        get() = _isLoginSuccess

    val isSitDown: UnPeekLiveData<Boolean>
        get() = _isSitDown

    val isisAllowedBet: UnPeekLiveData<Boolean>
        get() = _isAllowedBet
    val isShowGame: UnPeekLiveData<Boolean>
        get() = _isShowGame

    val isEnterGroup: UnPeekLiveData<Boolean>
        get() = _isEnterGroup

    val isLeaveGroup: UnPeekLiveData<Boolean>
        get() = _isLeaveGroup

    /*********正在玩的阶段***********/
    val toastErrorMessage: UnPeekLiveData<String>
        get() = _toastErrorMessage

    // 下注是否成功
    // 绑定使用 bettingMessage
    val isBettingSuccess: UnPeekLiveData<BettingResponsesBean>
        get() = _isBettingSuccess

    var bettingMessage: String = ""

    // 实现currentStage的observe，监听阶段变化
    // 绑定使用
    //  - 每个阶段
    //   - roundId 期号
    //  - countDown 倒计时
    //  - 结算结果 currentSettleResult
    val currentStage: UnPeekLiveData<GameStage>
        get() = _currentStage

    // 监听续压和加倍的状态
    val currentAgainDoubleState: UnPeekLiveData<AgainDoubleState>
        get() = _currentAgainDoubleState

    // 当前的历史记录
    var currentSettleResult: RoundInfoBean? = null

    // 实现balance的observe，监听余额变化
    // 该值需要缩小100倍，保留两位小数 用于展示
    val balance: UnPeekLiveData<Long>
        get() = _balance

    //临时计算用的余额 用于显示砝码的状态
    val tempBalance: UnPeekLiveData<Long>
        get() = _tempBalance

    // 实现syncAreaBetInfo的observe，监听default牌面的人数变化
    val syncAreaBetInfo: UnPeekLiveData<List<AreaBetBean>>
        get() = _syncAreaBetInfo

    //历史记录的列表
    val historyRounds: UnPeekLiveData<List<RoundInfoBean>>
        get() = _historyRounds


    /*********玩完一局的阶段***********/
    // 结算阶段使用
    // 开奖注区，用于展示注区的闪闪动画
    var lotteryResultList: ArrayList<Betting>? = null //开奖注区

    // 结算阶段使用
    // 净收入，用于展示中奖动画；使用时需要缩小100倍
    var netIncome: Int = 0

    // 结算阶段使用
    // 用户 中奖后 的面板砝码金额已经中奖注区
    var userLotteryResult: ArrayList<BettingRecordBean>? = null

    /*********其他阶段 或通用字段***********/
    var roundId: String = "" //期号
    var loginErrorMessage = ""
    var lastBetting: Betting? = null
    var gameList: MutableList<GameRes.MiniGameBasicInfo>? = null

    var tempMap: MutableMap<Betting, BettingRecordBean> = ConcurrentHashMap()
    var previousRoundId: String = "" //期号
    /*********End***********/

    fun setBettingSuccess(isSuccess: BettingResponsesBean) {
        _isBettingSuccess.postValue(isSuccess)
    }

    fun setLoginResult(isSuccess: Boolean) {
        _isLoginSuccess.postValue(isSuccess)
    }

    fun isSitDown(sitDown: Boolean) {
        _isSitDown.postValue(sitDown)
    }

    fun isShowGame(showGame: Boolean) {
        _isShowGame.postValue(showGame)
    }

    fun isAllowedBet(isAllowedBet: Boolean) {
        _isAllowedBet.postValue(isAllowedBet)
    }

    fun isEnterGroup(enter: Boolean) {
        _isEnterGroup.postValue(enter)
    }

    fun isLeaveGroup(leave: Boolean) {
        _isLeaveGroup.postValue(leave)
    }

    fun changeBalance(b: Long) {
        _balance.postValue(b)
    }

    fun changeTempBalance(balance: Long) {
        if (isMainThread) {
            _tempBalance.value = balance
        } else {
            _tempBalance.postValue(balance)
        }
    }

    fun deductTempBalance(money: Int) {
        val balance = _tempBalance.value!! - money
        if (isMainThread) {
            _tempBalance.value = balance
        } else {
            _tempBalance.postValue(balance)
        }
    }

    fun returnTempBalance(money: Int) {
        val balance = _tempBalance.value!! + money
        if (isMainThread) {
            _tempBalance.value = balance
        } else {
            _tempBalance.postValue(balance)
        }
    }

    fun changeStage(stage: GameStage) {
        _currentStage.postValue(stage)
    }

    fun changeAgainDoubleState(state: AgainDoubleState) {
        _currentAgainDoubleState.postValue(state)
    }

    fun changeAreaBetInfo(info: List<AreaBetBean>) {
        _syncAreaBetInfo.postValue(info)
    }

    fun clearTrends(ids: List<Int>) {
        _clearTrendsIds.postValue(ids)
    }

    fun addHistoryRounds(history: List<RoundInfoBean>) {
        _historyRounds.postValue(history)
    }

    fun addHistoryRound(item: RoundInfoBean) {
        currentSettleResult = item
        val history = _historyRounds.value ?: listOf()
        _historyRounds.postValue(history + item)
    }

    fun setToastErrorMessage(msg: String) {
        _toastErrorMessage.postValue(msg)
    }

    private val countDownHelper:CountDownHelper = CountDownHelper()
    var countDown:Int by countDownHelper::countDown
    val countDownSecondsLD: UnPeekLiveData<Int> by countDownHelper::countDownSecondsLD
    val isCountDownStart by countDownHelper::isCountDownStart
    //设置游戏大厅数据
    @UiThread fun setMoreGames(data: List<GameHallItem>) {
        _moreGames.value = data
    }
    //更新游戏大厅在线人数·
    @UiThread fun setMoreGameOnlines(onlines:List<Int>){
        val games = _moreGames.value ?: return
        games.forEachIndexed {index,item->
            if(index >= onlines.size) return
            item.online = onlines[index]
        }
        _moreGames.value = games
    }
    //控制隐藏Fast3MainView
    val fast3MainFloatVisible: UnPeekLiveData<Boolean> = UnPeekLiveData()
}
