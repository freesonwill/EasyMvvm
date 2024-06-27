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
import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel.Stage
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.getColor
import kotlin.math.roundToInt

class Fast3ViewModel : BaseViewModel() {
    var betOkClick: UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: UnPeekLiveData<Boolean> = UnPeekLiveData()

    var moneyAnimCallback: MoneyAnimCallback? = null
    var updateAreaViewMap : UnPeekLiveData<Int> = UnPeekLiveData()
    //    val historyResultBeans: MutableList<HistoryResultBean> by lazy { mutableListOf() }
    val userLotteryResultLiveData :UnPeekLiveData<ArrayList<Betting>> =UnPeekLiveData()

    val historyResultBeanLD: LiveData<HistoryResultBean> by lazy { UnPeekLiveData() }
    val homeTimeSeconds: LiveData<Int> by lazy { gameAboutModel.countDownSecondsLD }
    val homeTimeColorLD:LiveData<Int> by lazy { Transformations.map(this.homeTimeSeconds){
        if(it <= 5) return@map getColor(R.color.c_F34D41)
        if(it <= 10) return@map getColor(R.color.c_FFCB15)
        return@map getColor(R.color.c_62DF57)
    } }

    //游戏状态

    val gameState: GameState get() = when(gameAboutModel.currentStage.value){
        Stage.NEW -> GameState.Betting
        Stage.DEAL -> GameState.Settling
        Stage.SETTLE -> GameState.Drawing
        else ->GameState.Init
    }

    //是否可点击
    val isClickOperationLD: LiveData<Boolean> by lazy { UnPeekLiveData(true) }
    var isClickOperation: Boolean
        get() = isClickOperationLD.value!!
        set(value) {
            (isClickOperationLD as UnPeekLiveData).value = value
        }

    val homeTimeVisibility by lazy {
        Transformations.map(gameAboutModel.currentStage) {
            return@map when (it) {
                Stage.SETTLE -> View.GONE
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
        return@map it.formatRealMoney()
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
    val countDown:Long
        get(){
            Log.d(TAG,"countDown get ${gameAboutModel.countDown}")
            //return GameManager.instance.countDown
            return gameAboutModel.countDown.toLong()
        }
    val isCountDownStart:Boolean get() = gameAboutModel.isCountDownStart
    val playAlphaAnimationLD by lazy { UnPeekLiveData(false) }

    /**
     * 每次点击扣钱，但是不显示出来，确定后才把这个金额显示在真实钱上
     */
    var temporaryCurrentMoney: Int = 500000

    /** 开奖动画次数 **/
    val prizeAnimCount = 5

    /**
     * 开奖动画时间(ms)
     */
    val prizeAnimTime = 800L

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
                (homeTimeSeconds as UnPeekLiveData).value = seconds
            }

            override fun onCountDownFinish(state: GameState) {
                (homeTimeSeconds as UnPeekLiveData).value = 0
            }

            override fun onGameStateChanged(oldValue: GameState, newValue: GameState) {
                Log.d(TAG, "onGameStateChanged run on $isMainThread $oldValue-->$newValue")
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
        noteList.add(SelectAnnotationBean(money = 1000, select = true))
        noteList.add(SelectAnnotationBean(money = 5000))
        noteList.add(SelectAnnotationBean(money = 10000))
        noteList.add(SelectAnnotationBean(money = 20000))
        noteList.add(SelectAnnotationBean(money = 50000))
        noteList.add(SelectAnnotationBean(money = 100000))
        noteList.add(SelectAnnotationBean(money = 200000))
        noteList.add(SelectAnnotationBean(money = 500000))
        noteList.add(SelectAnnotationBean(money = 1000000))
        noteList.add(SelectAnnotationBean(money = 2000000))
        noteList.add(SelectAnnotationBean(money = 5000000))
        noteList.add(SelectAnnotationBean(money = 10000000))
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"~~~~~~~~~OnCleared")
    }

    fun clear() {

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