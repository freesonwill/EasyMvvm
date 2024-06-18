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
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.MutablePair
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

class Fast3ViewModel:BaseViewModel() {
    private val TAG = "Fast3ViewModel"
    var betOkClick:UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick:UnPeekLiveData<Boolean> = UnPeekLiveData()

    var anchorMoneyView:WeakReference<MoneyOKView> ?=null

    //<areaCode,<money,View>>
    var savedMoneyMap:MutableMap<Int,MutablePair<Int,WeakReference<MoneyOKView>>> = mutableMapOf()
    var tempMoneyMap:MutableMap<Int,MutablePair<Int,WeakReference<MoneyOKView>>> = mutableMapOf()
    var moneyAnimCallback: MoneyAnimCallback?= null
    val historyResultBeans: LiveData<List<HistoryResultBean>> by lazy { UnPeekLiveData() }
    private val countdownTime = 10_000
    val homeTime:LiveData<Int> by lazy { UnPeekLiveData(countDownSeconds) }
    private val countDownSeconds:Int get() = countdownTime/1000
    val gameStateLV:LiveData<GameState> by lazy { UnPeekLiveData( GameState.Init) }
    val isClickOperation by lazy { UnPeekLiveData<Boolean>() }
    val gameState:GameState get() { return gameStateLV.value!! }
    val homeTimeVisibility by lazy { Transformations.map(this.gameStateLV){
            return@map when(it){
                GameState.Drawing -> View.VISIBLE
                else -> View.VISIBLE
            }
        }
    }
    //========================================== Method =========================================//
    override fun onInit() {
        val list = ArrayList<HistoryResultBean>()
        for (c in 0 until 20) {
            list.add(HistoryResultBean(result = listOf(c % 6+1, (c + 1) % 6+1, (c + 2) % 6+1)))
        }
        (historyResultBeans as UnPeekLiveData).value = list
        GameManager.instance.setLiveStatusListener("home",object : IGameListener{

            override fun onCountdown(time: Long) {
                val seconds = (time.toFloat() / 1000).roundToInt()
                (homeTime as UnPeekLiveData).value = seconds
            }
            override fun onFinish(state: GameState) {
                (homeTime as UnPeekLiveData).value = 0
            }

            override fun onGameStateChanged(oldValue: GameState, newValue: GameState) {
                (gameStateLV as UnPeekLiveData).value = newValue
            }
        })
        GameManager.instance.startBetting()
        registerDataGC {
            Log.d(TAG,"removeLiveStatusListener home")
            GameManager.instance.removeLiveStatusListener("home")
        }

    }
    fun startBetting() {
        GameManager.instance.startBetting()
    }
    fun startSettling() {
        GameManager.instance.startSettling()
    }
    fun startDrawing() {
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

    interface MoneyAnimCallback{
        fun startAnim(x: Float, y: Float,isCentered: Boolean = false, speed: Long = 300, areaView: GameAreaView,endCallBack: (() -> Unit)?)
    }
}