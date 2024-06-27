package com.cn.game.sdk2.websocket.imp

import androidx.lifecycle.LifecycleOwner
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.balance
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.copy
import com.cn.game.sdk2.websocket.copyFrom
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.isEmpty
import com.cn.game.sdk2.websocket.isNotEmpty
import com.cn.game.sdk2.websocket.miniGameId
import com.cn.game.sdk2.websocket.previousSuccess
import com.xcjh.base_lib.utils.loge
import game.mod.proc.yf.proto.req.GameReq

class UIMethodImpl private constructor(client: GameSocketClient) : GameServiceImp(client) {

    companion object {
        @JvmStatic
        fun generate(client: GameSocketClient): UIMethodImpl {
            return UIMethodImpl(client)
        }
    }

    init {
        GameSocketManager.getInstance()?.setGameServerMessageConvertFactory(this)
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
     * @see [pushHistoryOfBetAction] 历史记录按钮
     *
     * @see [pushCustomerServiceAction] 联系客服按钮
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
        block: (isMoneyEnough: Boolean, result: BettingRecordBean?) -> Unit
    ) {
        tempMoney += recordBean.money


        val currentMoney = recordBean.money
        val tempMoney = if (bettingListTemp.containsKey(recordBean.bettingArea)) {
            bettingListTemp[recordBean.bettingArea]?.money!!  //同注区已有临时下注
        } else {
            0
        }
        val confirmedMoney = if (bettingListConfirmed.containsKey(recordBean.bettingArea)) {
            bettingListConfirmed[recordBean.bettingArea]?.money!!  // 同注区已有确认下注
        } else {
            0
        }
        val tempConfirmedMoney = if (bettingListTempConfirmed.containsKey(recordBean.bettingArea)) {
            bettingListTempConfirmed[recordBean.bettingArea]?.money!!// 同注区已有临时确认下注
        } else {
            0
        }
        val countMoney: Int =
            currentMoney + tempMoney + confirmedMoney + tempConfirmedMoney //本次下注后页面上应该显示的总金额
        //跟新again和double
        gameAboutModel.setOnceCountMoney(getPanelAllMoney())

        recordBean.money = currentMoney + tempMoney
        bettingListTemp[recordBean.bettingArea] = recordBean
        val uiBean = recordBean.copy()
        uiBean.money = countMoney
        block(isMoneyEnough(), uiBean)
    }

    /**
     *  取消下注
     *   - 清空临时下注集合
     *   - 返回已确认下注集合
     *  @param block 取消下注后返回已确认的下注
     */
    fun cancelBetting(block: (result: List<BettingRecordBean>?) -> Unit) {

        //----清空临时数据
        tempMoney = 0
        bettingListTemp.clear()
        //----
        //跟新again和double
        gameAboutModel.setOnceCountMoney(getPanelAllMoney())
        //返回已确认的集合
        bettingListConfirmed.isNotEmpty {
            val confirmedList = ArrayList<BettingRecordBean>()
            it.forEach { (_, bettingRecordBean) ->
                bettingRecordBean.money
                confirmedList.add(bettingRecordBean)
            }
            block(confirmedList)
        }.isEmpty {
            block(null)
        }
    }

    fun commitBetting() {
        if (previousSuccess) {
            //等有返回结果后 再赋值成true
            previousSuccess = false
            val betReq = GameReq.BetReq.newBuilder()
            betReq.setMiniGameId(miniGameId)
            bettingListTemp.forEach { (betting, bettingRecordBean) ->
                "注区${betting.number},下注金额：${bettingRecordBean.money}".loge("GameService-确认下注")
                val areaBetReq = GameReq.AreaBetReq.newBuilder().setAreaCode(betting.number)
                    .setBetScore(bettingRecordBean.money).build()
                betReq.addAreaBet(areaBetReq)
            }

            //--- 保存临时数据到中间态 清空临时数据
            bettingListTempConfirmed.clear()
            bettingListTempConfirmed copyFrom bettingListTemp.copy()
            confirmTempMoney = tempMoney
            tempMoney = 0
            bettingListTemp.clear()
            //---

            val build = betReq.build()
            bet(build)
        } else {
            "下注228：上次下注还未返回".loge("addBetting")
            gameAboutModel.setBettingSuccess(false)
        }
    }

    /**
     * 续压
     * 1，上一句的总额就是这一句临时额度 currentTempCountMoney
     */
    fun againBetting(): Map<Betting, BettingRecordBean> {
        //1
        tempMoney = againCountMoney

        bettingListTemp copyFrom againBettingList
        return againBettingList
    }

    /**
     * 加倍
     * 加倍后的总金额算法：@doubleMoney 只是用于传入接口的金额
     *    currentTempCountMoney * 2 + currentConfirmCountMoney
     */
    fun doubleBetting(block: (isMoneyEnough: Boolean, result: Map<Betting, BettingRecordBean>?) -> Unit) {
        if (doubleMoney < balance) {
            val tempCopy = bettingListTemp.copy()
            val confirmCopy = bettingListConfirmed.copy()
            val tempConfirmCopy = bettingListTempConfirmed.copy()
            tempCopy.mapValues {
                it.value.money *= 2
            }
            val uiMap = tempCopy.copy()
            confirmCopy.forEach {
                val confirmMoney = it.value.money
                if (tempCopy.containsKey(it.key)) {

                    val uiBean = uiMap[it.key]!!.copy()
                    uiBean.money += confirmMoney * 2
                    uiMap[it.key] = uiBean //页面

                    val dataBean = tempCopy[it.key]!!.copy()
                    dataBean.money += confirmMoney
                    tempCopy[it.key] = dataBean
                } else {
                    val uiBean = uiMap[it.key]!!.copy()
                    uiBean.money += confirmMoney * 2
                    uiMap[it.key] = uiBean //页面

                    tempCopy[it.key] = it.value.copy()
                }
            }
            tempConfirmCopy.forEach {
                val confirmMoney = it.value.money
                if (tempCopy.containsKey(it.key)) {

                    val uiBean = uiMap[it.key]!!.copy()
                    uiBean.money += confirmMoney * 2
                    uiMap[it.key] = uiBean //页面

                    val dataBean = tempCopy[it.key]!!.copy()
                    dataBean.money += confirmMoney
                    tempCopy[it.key] = dataBean
                } else {
                    val uiBean = uiMap[it.key]!!.copy()
                    uiBean.money += confirmMoney * 2
                    uiMap[it.key] = uiBean //页面

                    tempCopy[it.key] = it.value.copy()
                }
            }
            bettingListTemp.clear()
            bettingListTemp copyFrom tempCopy
            tempMoney = confirmTempMoney + confirmMoney + tempMoney * 2
            block(true, uiMap)
        } else {
            block(false, null)
        }
    }

    /**
     * 历史记录按钮
     */
    fun pushHistoryOfBetAction() {
        appListener?.historyOfBetAction()
    }

    /**
     * 联系客服按钮
     */
    fun pushCustomerServiceAction() {
        appListener?.customerServiceAction()
    }

    /**
     * step1: 判断是不是新的一局
     * step2: 判断能不能again (代表上一局有数据，并且余额足够)
     * step3: 判断牌面上是否有下注
     *  step1 = false 无法续压
     *
     */
    fun observeAgainDoubleState(owner: LifecycleOwner) {
        gameAboutModel.currentStage.observe(owner) {
            curStage = it
            checkAgain()
        }
        gameAboutModel.onceCountMoney.observe(owner) {
            onceCountMoney = it
            checkAgain()
        }
        gameAboutModel.isMeetAgain.observe(owner) {
            isMeetAgain = it
            checkAgain()
        }
    }

}