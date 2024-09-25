package com.cn.game.sdk2.ui.viewmodel.fast3

import com.cn.game.sdk2.data.enums.ChipBean
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib2.base.BaseViewModel


/**
 * 原Fast3ViewModel，之後只負責處理共通注區邏輯，並與Main交互
 */
class GameViewModel : BaseViewModel() {

    var betOkClick: UnPeekLiveData<Boolean> = UnPeekLiveData()
    var betDeleteClick: UnPeekLiveData<Boolean> = UnPeekLiveData()

    var moneyAnimCallback: MoneyAnimCallback? = null

    /** 开奖动画次数 **/
    val prizeAnimCount = 5

    /**
     * 开奖动画时间(ms)
     */
    val prizeAnimTime = 500L

    fun emitMoneyAnim(
        x: Float,
        y: Float,
        speed: Long = 250,
        areaView: GameAreaView,
        betteBean: ChipBean,
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
            betteBean: ChipBean,
            endCallBack: (() -> Unit)?
        )
    }
}