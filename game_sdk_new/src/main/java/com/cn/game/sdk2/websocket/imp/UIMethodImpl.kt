package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.utils.ThreadUtils.appListenerScope
import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.bean.AreaBetConfigBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.BettingResponsesBean
import com.cn.game.sdk2.websocket.bean.ObservableArrayList
import com.cn.game.sdk2.websocket.cancel
import com.cn.game.sdk2.websocket.constants.BettingState
import com.cn.game.sdk2.websocket.constants.BettingStatus
import com.cn.game.sdk2.websocket.double
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.generateUiBean
import com.cn.game.sdk2.websocket.getBeanById
import com.cn.game.sdk2.websocket.getMoneyByState
import com.cn.game.sdk2.websocket.merge
import com.cn.game.sdk2.websocket.verifyAdd
import com.cn.game.sdk2.websocket.verifyCommit
import com.cn.game.sdk2.websocket.verifyDouble
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import game.mod.proc.yf.proto.req.GameReq

internal class UIMethodImpl private constructor(client: GameSocketClient) : GameServiceImp(client) {

    companion object {
        private const val TAG = "UIMethodImpl"
        @JvmStatic
        fun generate(client: GameSocketClient): UIMethodImpl {
            return UIMethodImpl(client)
        }
    }

    init {
        GameSocketManager.getInstance()?.setGameServerMessageConvertFactory(this)

        bettingStepList.addOnListChangedCallback(object :
            ObservableArrayList.OnListChangedCallback {
            override fun change() {
                checkAgainNew()
            }
        })
    }

    /**
     * @see [addBetting] 临时下注
     *
     * @see [cancelBetting] 取消下注
     *
     * @see [commitBetting] 确认下注
     *
     * @see [againBetting] 续压
     *
     * @see [doubleBetting] 加倍下注
     *
     */


    /**
     * 临时下注
     * 该方法只在服务器状态是游戏中的时候才调用
     * @param recordBean 当前下注对象
     * @param block 下注成功回调
     *          - isMoneyEnough 当次下注余额是否足够
     *          - result 返回当次下注成功的结果，里面有当前的下注的总金额;当此参数为null时，表示上一次下注结果还未返回
     *          - isMoneyEnough = false 并且 result = null 说明上一次确认下注还未返回结果
     */
    fun addBetting(
        recordBean: BettingRecordBean,
        isFirstAdd: Boolean,
        block: (isMoneyEnough: BettingState, result: BettingRecordBean?, areaLimit: AreaBetConfigBean?) -> Unit
    ) {
        bettingStepList.add(recordBean)
        val verifyResult = bettingStepList.verifyAdd(
            recordBean, currentConfig?.getBeanById(recordBean.bettingArea)
        )
        when (verifyResult) {
            BettingState.GO_ON -> {
                if (isFirstAdd) {
                    removePreviousBettingBean()
                }
                tempLastBetting = recordBean.bettingArea
                gameAboutModel.deductTempBalance(recordBean.money)
                block(
                    BettingState.GO_ON,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    null
                )
            }

            BettingState.OFFSET_MAX -> {
                bettingStepList.removeLast()
                bettingStepList.modify()
                block(
                    BettingState.OFFSET_MAX,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    currentConfig?.getBeanById(recordBean.bettingArea)
                )
            }

            BettingState.NO_MONEY -> {
                bettingStepList.removeLast()
                bettingStepList.modify()
                block(
                    BettingState.NO_MONEY,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    null
                )

                appListenerScope.launchWithCustomContext(TAG) {
                    appListener?.onInsufficientBalance()
                }
            }

            BettingState.NO_MONEY_50 -> {
                bettingStepList.removeLast()
                bettingStepList.modify()
                block(
                    BettingState.NO_MONEY_50,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    null
                )

                appListenerScope.launchWithCustomContext(TAG) {
                    appListener?.onInsufficientBalance()
                }
            }

            else -> {}
        }
    }

    private fun removePreviousBettingBean() {
        if (bettingStepList.isNotEmpty()) {
            val roundId = gameAboutModel.roundId
            if (bettingStepList.first().roundId != roundId) {
                bettingStepList.apply {
                    val iterator = iterator()
                    while (iterator.hasNext()) {
                        if (roundId != iterator.next().roundId) {
                            iterator.remove()
                        }
                    }
                }
            }
        }


    }

    /**
     *  取消下注
     *   - 清空临时下注集合
     *   - 返回已确认下注集合
     *  @param block 取消下注后返回已确认的下注
     */
    fun cancelBetting(block: (result: List<BettingRecordBean?>) -> Unit) {
        val resultList = bettingStepList.cancel()
        block(resultList)
    }

    fun commitBetting(block: (isMoneyEnough: BettingState, result: AreaBetConfigBean?) -> Unit) {
        if (!gameAboutModel.isOpen) {
            block(BettingState.NO_NETWORK, null)
            return
        }
        if (previousSuccess) {
            val verifyCommitResult = bettingStepList.verifyCommit(currentConfig)
            if (verifyCommitResult != null) {
                block(BettingState.OFFSET_MIN, verifyCommitResult)
                return
            }
            //等有返回结果后 再赋值成true
            previousSuccess = false
            val betReq = GameReq.BetReq.newBuilder()
            betReq.setMiniGameId(miniGameId)
            bettingStepList.filter { it.state == BettingStatus.TEMP }.map {
                    it.state = BettingStatus.COMMITTING
                    it
                }.groupBy(BettingRecordBean::bettingArea).merge().forEach {
                    "注区${it.bettingArea.toastStr},下注金额：${it.money}".loge("GameService-确认下注")
                    val areaBetReq =
                        GameReq.AreaBetReq.newBuilder().setAreaCode(it.bettingArea.number)
                            .setBetScore(it.money).build()
                    betReq.addAreaBet(areaBetReq)

                }
            val build = betReq.build()
            bet(build)
            block(BettingState.GO_ON, null)
        } else {
            "下注228：上次下注还未返回".loge("addBetting")
            gameAboutModel.setBettingSuccess(BettingResponsesBean(false, 0))
        }
    }

    /**
     * 续压
     * 1，上一句的总额就是这一句临时额度 currentTempCountMoney
     */
    fun againBetting(): Map<Betting, BettingRecordBean> {
        //1
        againBettingMap.forEach {
//            limitMap[it.key] = it.value.money
            bettingStepList.add(it.value)
        }
//        gameAboutModel.changeTempBalance(balance - getPanelAllMoney())
        gameAboutModel.deductTempBalance(bettingStepList.getMoneyByState(BettingStatus.TEMP))
        return againBettingMap
    }

    /**
     * 加倍
     * 加倍后的总金额算法：@doubleMoney 只是用于传入接口的金额
     *    currentTempCountMoney * 2 + currentConfirmCountMoney
     */
    fun doubleBetting(block: (isMoneyEnough: BettingState, result: MutableMap<Betting, BettingRecordBean?>?, areaLimit: AreaBetConfigBean?) -> Unit) {
        bettingStepList.verifyDouble(currentConfig)?.let {
            if (it.noMoney) {
                block(BettingState.NO_MONEY, null, null)
            } else {
                block(BettingState.OFFSET_MAX, null, it.limitBean)
            }
        } ?: run {
            val double = bettingStepList.double()
            bettingStepList.modify()
            block(BettingState.GO_ON, double, null)
        }
    }



}