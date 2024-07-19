package com.cn.game.sdk2.ui.viewmodel.fast3

import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.BOOM_1
import com.cn.game.sdk2.websocket.bean.BOOM_2
import com.cn.game.sdk2.websocket.bean.BOOM_3
import com.cn.game.sdk2.websocket.bean.BOOM_4
import com.cn.game.sdk2.websocket.bean.BOOM_5
import com.cn.game.sdk2.websocket.bean.BOOM_6
import com.cn.game.sdk2.websocket.bean.BOOM_ALL
import com.cn.game.sdk2.websocket.bean.Betting
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
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel.Stage
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib2.base.BaseViewModel
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.getColor

class Fast3ViewModel : BaseViewModel() {


    var betOkClick: UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: UnPeekLiveData<Boolean> = UnPeekLiveData()

    var moneyAnimCallback: MoneyAnimCallback? = null
    val userLotteryResultLiveData: UnPeekLiveData<ArrayList<Betting>> = UnPeekLiveData()
    val cancelAreaFlickAnimLiveData = UnPeekLiveData<Boolean>()
    val addMoneyOkViewLiveData: UnPeekLiveData<Pair<GameAreaView, ViewGroup>> = UnPeekLiveData()

    val homeTimeSeconds: LiveData<Int> = gameAboutModel.countDownSecondsLD
    val homeTimeColorLD: LiveData<Int> by lazy {
        Transformations.map(this.homeTimeSeconds) {
            if (it <= 5) return@map getColor(R.color.c_F34D41)
            if (it <= 10) return@map getColor(R.color.c_FFCB15)
            return@map getColor(R.color.c_62DF57)
        }
    }

    //游戏状态
    val gameState: Stage? get() = gameAboutModel.currentStage.value
    var localGameStage: Stage? = null


    //是否可点击
    val isClickOperationLD: LiveData<Boolean> by lazy { UnPeekLiveData(true) }
    var isClickOperation: Boolean
        get() = isClickOperationLD.value!!
        set(value) {
            (isClickOperationLD as UnPeekLiveData).value = value
        }

    /**
     * 是否显示骰子的结果组合
     */
    var isShowResult: Boolean = false

    /**
     * 余额
     */
    var currentMoney: Long = gameAboutModel.balance.value ?: 0L

    /**
     * 投注的钱
     */
    var userLastSelectBetteBean: SelectAnnotationBean? = null
    val noteList: MutableList<SelectAnnotationBean> by lazy {
        userLastSelectBetteBean = SelectAnnotationBean(money = 1000, select = true)
        mutableListOf(
            SelectAnnotationBean(money = 1000, select = true),
            SelectAnnotationBean(money = 5000),
            SelectAnnotationBean(money = 10000),
            SelectAnnotationBean(money = 20000),
            SelectAnnotationBean(money = 50000),
            SelectAnnotationBean(money = 100000),
            SelectAnnotationBean(money = 200000),
            SelectAnnotationBean(money = 500000),
            SelectAnnotationBean(money = 1000000),
            SelectAnnotationBean(money = 2000000),
            SelectAnnotationBean(money = 5000000),
            SelectAnnotationBean(money = 10000000),
        )
    }
    val betteBean: SelectAnnotationBean
        get() {
            var selectedPosition = noteList.indexOfFirst { it.select }
            if (selectedPosition < 0) selectedPosition = 0
            return noteList[selectedPosition]
        }
    val countDown: Long
        get() {
            LogUtils.d(TAG, "countDown get ${gameAboutModel.countDown}")
            //return GameManager.instance.countDown
            return gameAboutModel.countDown.toLong()
        }
    val isCountDownStart: Boolean get() = gameAboutModel.isCountDownStart
    val playAlphaAnimationLD by lazy { UnPeekLiveData(false) }
    val navigationBarHeight by lazy { UnPeekLiveData(0) }

    /**
     * 每次点击扣钱，但是不显示出来，确定后才把这个金额显示在真实钱上
     */
    var temporaryCurrentMoney: Int = 500000

    /** 开奖动画次数 **/
    val prizeAnimCount = 5

    /**
     * 开奖动画时间(ms)
     */
    val prizeAnimTime = 500L

    //========================================== Method =========================================//

    override fun onCleared() {
        super.onCleared()
        LogUtils.d(TAG, "~~~~~~~~~OnCleared")
    }

    fun clear() {

    }


    fun emitMoneyAnim(
        x: Float,
        y: Float,
        speed: Long = 250,
        areaView: GameAreaView,
        betteBean: SelectAnnotationBean,
        endCallBack: (() -> Unit)? = null
    ) {
        moneyAnimCallback?.apply {
            startAnim(x, y, speed, areaView, betteBean, endCallBack)
        }
    }

    interface MoneyAnimCallback {
        fun startAnim(
            x: Float,
            y: Float,
            speed: Long,
            areaView: GameAreaView,
            betteBean: SelectAnnotationBean,
            endCallBack: (() -> Unit)?
        )
    }

    val dXDSBettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = DEFAULT_BIG()
            it[2] = DEFAULT_SMALL()
            it[3] = DEFAULT_SINGLE()
            it[4] = DEFAULT_DOUBLE()
            it[5] = BOOM_ALL()
        }
    }

    val leopardBettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = BOOM_1()
            it[2] = BOOM_2()
            it[3] = BOOM_3()
            it[4] = BOOM_4()
            it[5] = BOOM_5()
            it[6] = BOOM_6()
        }
    }

    val pairsDiceBettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = DOUBLE_1()
            it[2] = DOUBLE_2()
            it[3] = DOUBLE_3()
            it[4] = DOUBLE_4()
            it[5] = DOUBLE_5()
            it[6] = DOUBLE_6()
        }
    }

    val singleDiceBettingArray by lazy {
        SparseArray<Betting>().also {
            it[1] = SINGLE_1()
            it[2] = SINGLE_2()
            it[3] = SINGLE_3()
            it[4] = SINGLE_4()
            it[5] = SINGLE_5()
            it[6] = SINGLE_6()
        }
    }

    val sumTotalBettingArray by lazy {
        SparseArray<Betting>().also {
            it[4] = SUM_4()
            it[5] = SUM_5()
            it[6] = SUM_6()
            it[7] = SUM_7()
            it[8] = SUM_8()
            it[9] = SUM_9()
            it[10] = SUM_10()
            it[11] = SUM_11()
            it[12] = SUM_12()
            it[13] = SUM_13()
            it[14] = SUM_14()
            it[15] = SUM_15()
            it[16] = SUM_16()
            it[17] = SUM_17()
        }
    }

    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String = "x#.##"): String =
        CommonExt.multiplierStr(betting, format)

    @JvmOverloads
    fun multiplierSingStr(betting: Betting, format: String = "#.##"): String =
        CommonExt.multiplierStr(betting, format)
}