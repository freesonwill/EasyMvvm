package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cn.game.sdk2.data.bean.HistoryResultBean
import com.cn.game.sdk2.data.enums.GameState
import com.cn.game.sdk2.manager.GameManager
import com.cn.game.sdk2.manager.listener.IGameListener
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.utils.Ext.isMainThread
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.MutablePair
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

class Fast3ViewModel : BaseViewModel() {
    private val TAG = "Fast3ViewModel"
    var betOkClick: UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: UnPeekLiveData<Boolean> = UnPeekLiveData()

    var anchorMoneyView: WeakReference<MoneyOKView>? = null

    //<areaCode,<money,View>>
    var savedMoneyMap: MutableMap<Int, MutablePair<Int, WeakReference<MoneyOKView>>> =
        mutableMapOf()
    var tempMoneyMap: MutableMap<Int, MutablePair<Int, WeakReference<MoneyOKView>>> = mutableMapOf()
    var moneyAnimCallback: MoneyAnimCallback? = null
    val historyResultBeans: MutableList<HistoryResultBean> by lazy { mutableListOf() }
    val historyResultBeanLD: LiveData<HistoryResultBean> by lazy { UnPeekLiveData()}
    private val countdownTime = 10_000
    val homeTime: LiveData<Int> by lazy { UnPeekLiveData(countDownSeconds) }
    private val countDownSeconds: Int get() = countdownTime / 1000
    val gameStateLV: LiveData<GameState> by lazy { UnPeekLiveData(GameState.Init) }
    val isClickOperation by lazy { UnPeekLiveData<Boolean>() }
    val gameState: GameState
        get() {
            return gameStateLV.value!!
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
    var isShowResult: Boolean = true
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
        })
        registerAutoGC {
            Log.d(TAG, "removeLiveStatusListener home")
            GameManager.instance.removeLiveStatusListener("home")
        }

    }

    fun startBetting() {
        GameManager.instance.startBetting()
    }

    fun startCountDown(time:Int) {
        GameManager.instance.startCountDownTimer(time)
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

    fun updateAnchorView(anchor: MoneyOKView){
        anchorMoneyView?.get()?.apply {
            hiddenTop()
        }
        anchor.showTop()
        anchorMoneyView = WeakReference(anchor)
    }

    fun addTemMoney(areaView: GameAreaView, betMoney: Int) {
        tempMoneyMap.apply {
            if (!containsKey(areaView.areaCode)) {
                put(
                    areaView.areaCode,
                    MutablePair(betMoney, WeakReference(areaView.moneyView)
                ))
            } else {
                get(areaView.areaCode)?.apply { first += betMoney }
            }
            get(areaView.areaCode)?.first?.let {
                val sum = savedMoneyMap[areaView.areaCode]?.first ?: 0
                areaView.moneyView.setShowMoney(it.plus(sum))
            }
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