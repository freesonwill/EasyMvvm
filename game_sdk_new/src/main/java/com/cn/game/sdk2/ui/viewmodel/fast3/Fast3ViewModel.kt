package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.bean.LocationClickPoint
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.GameManager
import com.cn.game.sdk2.manager.listener.IGameListener
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.gameAboutModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.getColor
import java.util.Locale
import kotlin.math.roundToInt

class Fast3ViewModel : BaseViewModel() {
    companion object {
        private val TAG = "Fast3ViewModel"

    }

    var betOkClick: UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: UnPeekLiveData<Boolean> = UnPeekLiveData()

    var moneyAnimCallback: MoneyAnimCallback? = null
    var updateAreaViewMap : UnPeekLiveData<Int> = UnPeekLiveData()

    //Todo viewModel不应该持有view的任何东西
//    val currentBettingRecordBeanLD: LiveData<Pair<BettingRecordBean, MoneyOKView>> by lazy { UnPeekLiveData() }
//    var currentBettingRecordBean: Pair<BettingRecordBean, MoneyOKView>?
//        set(value) {
//            (currentBettingRecordBeanLD as UnPeekLiveData).value = value
//        }
//        get() = currentBettingRecordBeanLD.value
    //var tempBetRecordMap:MutableMap<Int, MutablePair<BettingRecordBean, WeakReference<MoneyOKView>>> = mutableMapOf()

    //    val historyResultBeans: MutableList<HistoryResultBean> by lazy { mutableListOf() }
    val userLotteryResultLiveData :UnPeekLiveData<ArrayList<Betting>> =UnPeekLiveData()

    val historyResultBeanLD: LiveData<HistoryResultBean> by lazy { UnPeekLiveData() }
    val homeTime: LiveData<Int> by lazy { UnPeekLiveData(bettingCountDownTime/1000) }
    val homeTimeColorLD:LiveData<Int> by lazy { Transformations.map(this.homeTime){
        if(it <= 5) return@map getColor(R.color.c_F34D41)
        if(it <= 10) return@map getColor(R.color.c_FFCB15)
        return@map getColor(R.color.c_62DF57)
    } }

    //游戏状态
    val gameStateLV: LiveData<GameState> by lazy { UnPeekLiveData(GameState.Init) }
    val gameState: GameState get() = gameStateLV.value!!

    //是否可点击
    val isClickOperationLD: LiveData<Boolean> by lazy { UnPeekLiveData(true) }
    var isClickOperation: Boolean
        get() = isClickOperationLD.value!!
        set(value) {
            (isClickOperationLD as UnPeekLiveData).value = value
        }

    val homeTimeVisibility by lazy {
        Transformations.map(this.gameStateLV) {
            return@map when (it) {
                GameState.Drawing -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    /**
     * 是否显示骰子的结果组合
     */
    var isShowResult: Boolean = false

    /**
     * 余额
     */
    val currentMoneyLD: LiveData<String> = Transformations.map(gameAboutModel.balance) {
        if (it == null) return@map "--"
        return@map String.format(Locale.ROOT, "%.2f", it / 100f)
    }
    val currentMoney: String get() = currentMoneyLD.value ?: "--"

    /**
     *
     */
    val onGameAreaLocationClick: UnPeekLiveData<LocationClickPoint> by lazy { UnPeekLiveData() }

    /**
     * 投注的钱
     */
    var noteList = ArrayList<SelectAnnotationBean>()
    val betMoney: Int
        get() {
            val selectedPosition = noteList.indexOfFirst { it.select }
            return noteList[selectedPosition].money
        }

    /**
     * 每次点击扣钱，但是不显示出来，确定后才把这个金额显示在真实钱上
     */
    var temporaryCurrentMoney: Int = 500000

    /**
     * 开奖动画时间(ms)
     */
    val prizeAnimTime = 800L * 5

    /**
     * 下注倒计时(ms)
     */
    val bettingCountDownTime = 11_000

    /**
     * 结算倒计时(ms)
     */
    val settingCountDownTime = 3_000

    //========================================== Method =========================================//
    override fun onInit() {

        GameManager.instance.setLiveStatusListener("home", object : IGameListener {

            override fun onCountdown(time: Long) {
                val seconds = (time.toFloat() / 1000).roundToInt()
                (homeTime as UnPeekLiveData).value = seconds
            }

            override fun onCountDownFinish(state: GameState) {
                (homeTime as UnPeekLiveData).value = 0
            }

            override fun onGameStateChanged(oldValue: GameState, newValue: GameState) {
                Log.d(TAG, "onGameStateChanged run on $isMainThread $oldValue-->$newValue")
                (gameStateLV as UnPeekLiveData).value = newValue
            }

            override fun onDrawingResult(result: HistoryResultBean) {
                Log.d(TAG, "onDrawingResult run on $isMainThread result:$result")
                (historyResultBeanLD as UnPeekLiveData<HistoryResultBean>).value = result
            }
        }).let {
            registerAutoGC {
                Log.d(TAG, "removeLiveStatusListener home")
                GameManager.instance.removeLiveStatusListener("home")
            }
        }
        noteList.add(SelectAnnotationBean(money = 10, select = true))
        noteList.add(SelectAnnotationBean(money = 50))
        noteList.add(SelectAnnotationBean(money = 100))
        noteList.add(SelectAnnotationBean(money = 200))
        noteList.add(SelectAnnotationBean(money = 500))
        noteList.add(SelectAnnotationBean(money = 1000))
        noteList.add(SelectAnnotationBean(money = 2000))
        noteList.add(SelectAnnotationBean(money = 5000))
        noteList.add(SelectAnnotationBean(money = 10000))
        noteList.add(SelectAnnotationBean(money = 20000))
        noteList.add(SelectAnnotationBean(money = 50000))
        noteList.add(SelectAnnotationBean(money = 100000))
    }

    fun startBetting() {
        GameManager.instance.startBetting()
    }

    fun startCountDown(time: Int) {
        GameManager.instance.startCountDownTimer(time)
    }

    fun clear() {
        GameManager.instance.stopCountDown()
        GameManager.instance.reset()
//        currentBettingRecordBean = null
    }

    fun startSettling() {
        GameManager.instance.startSettling()
    }

    suspend fun startDrawing() {
        GameManager.instance.startDrawing()
    }

    fun emitMoneyAnim(
        x: Float,
        y: Float,
        speed: Long = 300,
        areaView: GameAreaView,
        endCallBack: (() -> Unit)? = null
    ) {
        moneyAnimCallback?.apply {
            startAnim(x, y, speed, areaView, endCallBack)
        }
    }

    interface MoneyAnimCallback {
        fun startAnim(
            x: Float,
            y: Float,
            speed: Long = 300,
            areaView: GameAreaView,
            endCallBack: (() -> Unit)?
        )
    }
}