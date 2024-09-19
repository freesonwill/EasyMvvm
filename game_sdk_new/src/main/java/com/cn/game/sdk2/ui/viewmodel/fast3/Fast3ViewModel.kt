package com.cn.game.sdk2.ui.viewmodel.fast3

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.utils.BettingAreaUtil.getBoomBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getDefaultBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getDoubleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getSingleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getSumBets
import com.cn.game.sdk2.utils.BettingAreaUtil.toSpareArray
import com.cn.game.sdk2.utils.ext.CommonExt
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.constants.GameStage
import com.cn.game.sdk2.websocket.gameAboutModel
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
    val updateMoneyViewLiveData: UnPeekLiveData<GameAreaView> = UnPeekLiveData()

    val homeTimeSeconds: LiveData<Int> = gameAboutModel.countDownSecondsLD
    val homeTimeColorLD: LiveData<Int> by lazy {
        Transformations.map(this.homeTimeSeconds) {
            if (it <= 5) return@map getColor(R.color.c_F34D41)
            if (it <= 10) return@map getColor(R.color.c_FFCB15)
            return@map getColor(R.color.c_62DF57)
        }
    }

    //游戏状态
    internal val gameState: GameStage? get() = gameAboutModel.currentStage.value
    internal var localGameStage: GameStage? = null


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

    val countDown: Long
        get() {
            LogUtils.dTag(TAG, "countDown get ${gameAboutModel.countDown}")
            //return GameManager.instance.countDown
            return gameAboutModel.countDown.toLong()
        }
    val isCountDownStart: Boolean get() = gameAboutModel.isCountDownStart

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
        LogUtils.dTag(TAG, "~~~~~~~~~OnCleared")
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
        getDefaultBets().toSpareArray()
    }

    val leopardBettingArray by lazy {
        getBoomBets().toSpareArray()
    }

    val pairsDiceBettingArray by lazy {
        getDoubleBets().toSpareArray()
    }

    val singleDiceBettingArray by lazy {
        getSingleBets().toSpareArray()
    }

    val sumTotalBettingArray by lazy {
        getSumBets().toSpareArray(4)
    }

    @JvmOverloads
    fun multiplierStr(betting: Betting, format: String = "×#.##"): String =
        CommonExt.multiplierStr(betting, format)

    @JvmOverloads
    fun multiplierSingStr(betting: Betting, format: String = "#.##"): String =
        CommonExt.multiplierStr(betting, format)
}