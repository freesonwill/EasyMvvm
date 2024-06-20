package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.bean.LocationClickPoint
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.GameManager
import com.cn.game.sdk2.manager.listener.IGameListener
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.utils.Ext.isMainThread
import com.cn.game.sdk2.utils.MyGameManager
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.MutablePair
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

class Fast3ViewModel : BaseViewModel() {

    private val TAG = "Fast3ViewModel"
    var betOkClick: UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: UnPeekLiveData<Boolean> = UnPeekLiveData()

    var moneyAnimCallback: MoneyAnimCallback? = null

    var currentBettingRecordBean:BettingRecordBean ?= null
    var tempBetRecordMap:MutableMap<Int, MutablePair<BettingRecordBean, WeakReference<MoneyOKView>>> = mutableMapOf()

    val historyResultBeans: MutableList<HistoryResultBean> by lazy { mutableListOf() }
    val historyResultBeanLD: LiveData<HistoryResultBean> by lazy { UnPeekLiveData()}
    private val countdownTime = 20_000
    val homeTime: LiveData<Int> by lazy { UnPeekLiveData(countDownSeconds) }
    private val countDownSeconds: Int get() = countdownTime / 1000
    val gameStateLV: LiveData<GameState> by lazy { UnPeekLiveData(GameState.Init) }
    val isClickOperationLD by lazy { UnPeekLiveData<Boolean>(true) }
    val gameState: GameState get() = gameStateLV.value!!
    val isClickOperation: Boolean get() = isClickOperationLD.value!!
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
    var isShowResult: Boolean = true
    val currentMoney: LiveData<Int> by lazy { UnPeekLiveData(100000) }

    val onGameAreaLocationClick:UnPeekLiveData<LocationClickPoint> by lazy { UnPeekLiveData() }

    /**
     * 投注的钱
     */
    var noteList = ArrayList<SelectAnnotationBean>()
    val betMoney:Int
        get() {
            val selectedPosition = noteList.indexOfFirst { it.select }
            return noteList[selectedPosition].money
        }
    /**
     * 每次点击扣钱，但是不显示出来，确定后才把这个金额显示在真实钱上
     */
    var temporaryCurrentMoney: Int = 500000
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
        }).let { registerAutoGC {
            Log.d(TAG, "removeLiveStatusListener home")
            GameManager.instance.removeLiveStatusListener("home")
        }}
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

    fun startCountDown(time:Int) {
        GameManager.instance.startCountDownTimer(time)
    }

    fun stopCountDown() {
        GameManager.instance.stopCountDown()
    }

    fun startSettling() {
        GameManager.instance.startSettling()
    }

    suspend fun startDrawing() {
        GameManager.instance.startDrawing()
    }

    fun emitMoneyAnim(x: Float, y: Float, isCentered: Boolean = false, speed: Long = 300, areaView: GameAreaView,endCallBack:(()->Unit)?=null){
        moneyAnimCallback?.apply {
            startAnim(x, y, isCentered, speed, areaView,endCallBack)
        }
    }

    interface MoneyAnimCallback {
        fun startAnim(
            x: Float,
            y: Float,
            isCentered: Boolean = false,
            speed: Long = 300,
            areaView: GameAreaView,
            endCallBack: (() -> Unit)?
        )
    }
}