package com.cn.game.sdk2.websocket.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cn.game.sdk2.manager.GameManager
import com.cn.game.sdk2.manager.listener.IGameListener
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel

class GameAboutModel : BaseViewModel() {
    enum class Stage {
        NEW, DEAL, SETTLE
    }

    enum class AgainDoubleState {
        NUll, AGAIN, DOUBLE
    }

    private val _currentStage = UnPeekLiveData<Stage>()
    private val _currentAgainDoubleState = MutableLiveData<AgainDoubleState>()
    private val _balance = MutableLiveData<Long>()
    private val _syncAreaBetInfo = MutableLiveData<List<AreaBetBean>>()
    private val _clearTrendsIds = MutableLiveData<List<Int>>()
    private val _historyRounds = MutableLiveData<List<RoundInfoBean>>()

    private val _isMeetAgain = MutableLiveData<Boolean>()
    private val _isLoginSuccess = MutableLiveData<Boolean>()
    private val _isSitDown = MutableLiveData<Boolean>()
    private val _isEnterGroup = MutableLiveData<Boolean>()
    private val _isLeaveGroup = MutableLiveData<Boolean>()

    private val _isBettingSuccess = MutableLiveData<Boolean>()
    private val _toastErrorMessage = MutableLiveData<String>()

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

    val isLoginSuccess: LiveData<Boolean>
        get() = _isLoginSuccess

    val isSitDown: LiveData<Boolean>
        get() = _isSitDown

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
    val isBettingSuccess: LiveData<Boolean>
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

    fun setBettingSuccess(isSuccess: Boolean) {
        _isBettingSuccess.value = isSuccess
    }

    fun setLoginResult(isSuccess: Boolean) {
        _isLoginSuccess.value = isSuccess
    }

    fun isSitDown(sitDown: Boolean) {
        _isSitDown.value = sitDown
    }

    fun isEnterGroup(enter: Boolean) {
        _isEnterGroup.value = enter
    }

    fun isLeaveGroup(leave: Boolean) {
        _isLeaveGroup.value = leave
    }

    fun changeMeetAgain(canAgain: Boolean) {
        _isMeetAgain.value = canAgain
    }

    fun changeBalance(b: Long) {
        _balance.value = b
    }

    fun changeStage(stage: Stage) {
        _currentStage.value = stage
    }

    fun changeAgainDoubleState(state: AgainDoubleState) {
        _currentAgainDoubleState.value = state
    }

    fun changeAreaBetInfo(info: List<AreaBetBean>) {
        _syncAreaBetInfo.value = info
    }

    fun clearTrends(ids: List<Int>) {
        _clearTrendsIds.value = ids
    }

    fun addHistoryRounds(history: List<RoundInfoBean>) {
        _historyRounds.value = history
    }

    fun addHistoryRound(item: RoundInfoBean) {
        val history = _historyRounds.value ?: listOf()
        _historyRounds.value = history + item
    }

    fun setToastErrorMessage(msg: String) {
        _toastErrorMessage.value = msg
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
        _onceCountMoney.value = money
    }

    var miniGameId: Int = 0
    var countDown: Int = 0 //阶段倒计时
        set(value) {
            field = value
            Log.d(TAG,"countDown set:${field}")
            _countDownSetStampTime = System.currentTimeMillis()
            GameManager.instance.startCountDownTimer(value.toLong(), lis = object :IGameListener{
                override fun onCountdown(time: Long) {
                    super.onCountdown(time)
                    _countDownSecondsLD.value = (time/1000).toInt()
                }
            })
        }
        get() {
            val elapsed = System.currentTimeMillis() - _countDownSetStampTime
            Log.d(TAG,"countDown elapsed:${elapsed}")
            return (field -elapsed).toInt()
        }
    private val _countDownSecondsLD:UnPeekLiveData<Int> = UnPeekLiveData(0)
    val countDownSecondsLD:LiveData<Int> = _countDownSecondsLD
    private var _countDownSetStampTime:Long = 0L
    val isCountDownStart get() = (System.currentTimeMillis() - _countDownSetStampTime) < 50
    var roundId: String = "" //期号
    var loginErrorMessage = ""
    var lastBetting: Betting? = null
}