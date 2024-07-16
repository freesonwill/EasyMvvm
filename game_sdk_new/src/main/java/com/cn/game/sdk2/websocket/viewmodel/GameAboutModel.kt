package com.cn.game.sdk2.websocket.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cn.game.sdk2.manager.GameManager
import com.cn.game.sdk2.manager.listener.IGameListener
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.BettingResponsesBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib2.base.BaseViewModel
import kotlinx.coroutines.*

class GameAboutModel : BaseViewModel() {
    enum class Stage {
        NEW, DEAL, SETTLE
    }

    enum class AgainDoubleState {
        NUll, AGAIN, AGAIN_CAN_NOT_50, DOUBLE, DOUBLE_CAN_NOT, DOUBLE_CAN_NOT_50
    }

    enum class BettingState {
        GO_ON, NO_MONEY, OFFSET_MIN, OFFSET_MAX, NO_MONEY_50,NO_NETWORK
    }

    private val _currentStage = UnPeekLiveData<Stage>()
    private val _currentAgainDoubleState = MutableLiveData<AgainDoubleState>()
    private val _balance = MutableLiveData<Long>()
    private val _tempBalance = MutableLiveData<Long>()
    private val _syncAreaBetInfo = MutableLiveData<List<AreaBetBean>>()
    private val _clearTrendsIds = MutableLiveData<List<Int>>()
    private val _historyRounds = MutableLiveData<List<RoundInfoBean>>()

    private val _isMeetAgain = MutableLiveData<Boolean>()
    private val _isLoginSuccess = MutableLiveData<Boolean>()
    private val _isSitDown = MutableLiveData<Boolean>()
    private val _isEnterGroup = MutableLiveData<Boolean>()
    private val _isLeaveGroup = MutableLiveData<Boolean>()

    private val _isBettingSuccess = MutableLiveData<BettingResponsesBean>()
    private val _toastErrorMessage = MutableLiveData<String>()
    private val _isShowGame = MutableLiveData<Boolean>()
    private val _isAllowedBet = MutableLiveData<Boolean>(true )

    var isOpen:Boolean = false

    //设置人为豹子
    var manualLeopard:Boolean = false

    //是否是主播： 主播只能看到"热门"游戏分类，"热门"分类中以后只会放sdk游戏，在大厅弹窗处，主播端看不到其他的tab和瓦力游戏。
    var isAnchor: Boolean = false

    //如果不需要显示(isShowHistoryAndCustomer = false)，则主播端的更多只显示切换游戏和帮助。
    var isShowHistoryAndCustomer: Boolean = true

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

    val isLoginSuccess
        get() = _isLoginSuccess

    val isSitDown: LiveData<Boolean>
        get() = _isSitDown

    val isisAllowedBet: LiveData<Boolean>
        get() = _isAllowedBet
    val isShowGame: LiveData<Boolean>
        get() = _isShowGame

    val isEnterGroup: LiveData<Boolean>
        get() = _isEnterGroup

    val isLeaveGroup: LiveData<Boolean>
        get() = _isLeaveGroup

    /**
     * 接口返回错误信息 可能需要弹窗提示
     */
    val toastErrorMessage: LiveData<String>
        get() = _toastErrorMessage

    /**
     * 下注是否成功
     * 绑定使用 bettingMessage
     */
    val isBettingSuccess: LiveData<BettingResponsesBean>
        get() = _isBettingSuccess

    var bettingMessage: String = ""

    /**
     * 实现currentStage的observe，监听阶段变化
     * 绑定使用
     *  - 每个阶段
     *   - roundId 期号
     *   - countDown 倒计时
     *  - 结算结果 currentSettleResult
     */
    val currentStage: LiveData<Stage>
        get() = _currentStage

    val currentAgainDoubleState: LiveData<AgainDoubleState>
        get() = _currentAgainDoubleState

    var currentSettleResult: RoundInfoBean? = null

    /**
     * 实现balance的observe，监听余额变化
     * 该值需要缩小100倍，保留两位小数用于展示
     */
    val balance: LiveData<Long>
        get() = _balance

    val tempBalance: LiveData<Long>
        get() = _tempBalance

    /**
     * 实现syncAreaBetInfo的observe，监听default牌面的人数变化
     */
    val syncAreaBetInfo: LiveData<List<AreaBetBean>>
        get() = _syncAreaBetInfo

    /**
     * 暂时无用
     */
    val clearTrendsIds: LiveData<List<Int>>
        get() = _clearTrendsIds

    val historyRounds: LiveData<List<RoundInfoBean>>
        get() = _historyRounds

    /**
     * 监听isMeetAgain
     */
    val isMeetAgain: LiveData<Boolean>
        get() = _isMeetAgain

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

    fun changeMeetAgain(canAgain: Boolean) {
        _isMeetAgain.postValue(canAgain)
    }

    fun changeBalance(b: Long) {
        _balance.postValue(b)
    }

    fun changeTempBalance(balance: Long) {
        _tempBalance.postValue(balance)
    }

    fun changeStage(stage: Stage) {
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
        currentSettleResult = history.let { if (it.isEmpty()) null else it[it.size - 1] }
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

    private val _onceCountMoney = MutableLiveData<Int>()

    val onceCountMoney: LiveData<Int>
        get() = _onceCountMoney


    fun setOnceCountMoney(money: Int) {
        _onceCountMoney.postValue(money)
    }

    var miniGameId: Int = 0
    var countDown: Int = 0 //阶段倒计时
        set(value) {
            field = value - 0 //减去500ms延时
            Log.d(TAG, "countDown set:${value},isMainThread:${isMainThread}")
            _countDownSetStampTime = System.currentTimeMillis()
            ThreadUtils.runOnUiThread {
                GameManager.instance.startCountDownTimer(field.toLong(), lis = object : IGameListener {
                    override fun onCountdown(time: Long) {
                        super.onCountdown(time)
                        val t = (time / 1000f).toInt()
                        //Log.d(TAG, "countDown,isMainThread:${isMainThread},time:$t")
                        //onCountDown跟调用同一线程,这里不用post
                        _countDownSecondsLD.value = t
                    }
                })
            }
        }
        get() {
            val elapsed = System.currentTimeMillis() - _countDownSetStampTime
            Log.d(TAG, "countDown elapsed:${elapsed}")
            return (field - elapsed).toInt()
        }
    private val _countDownSecondsLD: UnPeekLiveData<Int> = UnPeekLiveData(0)
    val countDownSecondsLD: LiveData<Int> = _countDownSecondsLD
    private var _countDownSetStampTime: Long = 0L
    val isCountDownStart get() = (System.currentTimeMillis() - _countDownSetStampTime) < 50
    var roundId: String = "" //期号
    var loginErrorMessage = ""
    var lastBetting: Betting? = null

    //控制隐藏Fast3MainView
    val fast3MainFloatVisible: UnPeekLiveData<Boolean> = UnPeekLiveData<Boolean>()
}
