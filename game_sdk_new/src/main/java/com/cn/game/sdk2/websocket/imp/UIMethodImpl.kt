package com.cn.game.sdk2.websocket.imp

import androidx.lifecycle.LifecycleOwner
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.bean.AreaBetConfigBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.BettingResponsesBean
import com.cn.game.sdk2.websocket.bean.BettingStatus
import com.cn.game.sdk2.websocket.bean.ObservableArrayList
import com.cn.game.sdk2.websocket.cancel
import com.cn.game.sdk2.websocket.double
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.generateUiBean
import com.cn.game.sdk2.websocket.getBeanById
import com.cn.game.sdk2.websocket.getMoneyByState
import com.cn.game.sdk2.websocket.merge
import com.cn.game.sdk2.websocket.verifyAdd
import com.cn.game.sdk2.websocket.verifyCommit
import com.cn.game.sdk2.websocket.verifyDouble
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.xcjh.base_lib2.utils.loge
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

        bettingStepList.addOnListChangedCallback(object :ObservableArrayList.OnListChangedCallback{
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
        block: (isMoneyEnough: GameAboutModel.BettingState, result: BettingRecordBean?, areaLimit: AreaBetConfigBean?) -> Unit
    ) {

//        val currentMoney = recordBean.money
//        val areaTempMoney = if (bettingListTemp.containsKey(recordBean.bettingArea)) {
//            bettingListTemp[recordBean.bettingArea]?.money!!  //同注区已有临时下注
//        } else {
//            0
//        }
//        val confirmedMoney = if (bettingListConfirmed.containsKey(recordBean.bettingArea)) {
//            bettingListConfirmed[recordBean.bettingArea]?.money!!  // 同注区已有确认下注
//        } else {
//            0
//        }
//        val tempConfirmedMoney = if (bettingListTempConfirmed.containsKey(recordBean.bettingArea)) {
//            bettingListTempConfirmed[recordBean.bettingArea]?.money!!// 同注区已有临时确认下注
//        } else {
//            0
//        }
//
//        "currentMoney:${currentMoney}---areaTempMoney:$areaTempMoney----confirmedMoney:$confirmedMoney---tempConfirmedMoney:$tempConfirmedMoney".loge(
//            "test"
//        )
//        "balance:$balance".loge("test")
//        "tempBalance:${gameAboutModel.tempBalance.value}".loge("test")

        bettingStepList.add(recordBean)
        val verifyResult = bettingStepList.verifyAdd(
            recordBean, currentConfig?.getBeanById(recordBean.bettingArea)
        )
        when (verifyResult) {
            GameAboutModel.BettingState.GO_ON -> {
                tempLastBetting = recordBean.bettingArea
                gameAboutModel.deductTempBalance(recordBean.money)
                block(
                    GameAboutModel.BettingState.GO_ON,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    null
                )
            }

            GameAboutModel.BettingState.OFFSET_MAX -> {
                bettingStepList.removeLast()
                bettingStepList.modify()
                block(
                    GameAboutModel.BettingState.OFFSET_MAX,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    currentConfig?.getBeanById(recordBean.bettingArea)
                )
            }

            GameAboutModel.BettingState.NO_MONEY -> {
                bettingStepList.removeLast()
                bettingStepList.modify()
                block(
                    GameAboutModel.BettingState.NO_MONEY,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    null
                )
            }

            GameAboutModel.BettingState.NO_MONEY_50 -> {
                bettingStepList.removeLast()
                bettingStepList.modify()
                block(
                    GameAboutModel.BettingState.NO_MONEY_50,
                    bettingStepList.generateUiBean(recordBean.bettingArea),
                    null
                )
                appListener?.onInsufficientBalance()
            }

            else -> {}
        }

        /* val addFuture = addCanGoOn(recordBean)
         when (addFuture) {
             "继续" -> {
                 tempMoney += recordBean.money
                 tempLastBetting = recordBean.bettingArea
                 var xy: FloatArray? = null
                 if (bettingListTemp.containsKey(recordBean.bettingArea)) {
                     val viewXYTemporary = bettingListTemp[recordBean.bettingArea]?.viewXYTemporary
                     val all = viewXYTemporary?.all { it > 0 }
                     all?.let {
                         if (it) {
                             xy = viewXYTemporary
                         }
                     }
                 }
                 recordBean.money = currentMoney + areaTempMoney
                 xy?.let {
                     recordBean.viewXYTemporary = it
                 }
                 //跟新again和double
                 gameAboutModel.setOnceCountMoney(getPanelAllMoney())
                 bettingListTemp[recordBean.bettingArea] = recordBean
                 val uiBean = recordBean.copy()
                 val countMoney: Int =
                     currentMoney + areaTempMoney + confirmedMoney + tempConfirmedMoney //本次下注后页面上应该显示的总金额
                 uiBean.money = countMoney
                 limitMap[recordBean.bettingArea] = countMoney
                 gameAboutModel.changeTempBalance(balance - getPanelAllMoney())
                 block(GameAboutModel.BettingState.GO_ON, uiBean, null)
             }

             "限高" -> {
                 val uiBean = recordBean.copy()
                 uiBean.money = areaTempMoney + confirmedMoney + tempConfirmedMoney
                 block(
                     GameAboutModel.BettingState.OFFSET_MAX,
                     uiBean,
                     currentConfig?.getBeanById(recordBean.bettingArea)
                 )
             }

             "余额不足" -> {
                 val uiBean = recordBean.copy()
                 uiBean.money = areaTempMoney + confirmedMoney + tempConfirmedMoney
                 block(GameAboutModel.BettingState.NO_MONEY, uiBean, null)
             }

             "余额不足50" -> {
                 val uiBean = recordBean.copy()
                 uiBean.money = areaTempMoney + confirmedMoney + tempConfirmedMoney
                 block(GameAboutModel.BettingState.NO_MONEY_50, uiBean, null)
                 appListener?.onInsufficientBalance()
             }
         }*/


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

//        //----清空临时数据
//        tempMoney = 0
//        bettingListTemp.clear()
//        limitMap.clear()
//        gameAboutModel.changeTempBalance(balance)
//        //----
//        //跟新again和double
//        gameAboutModel.setOnceCountMoney(getPanelAllMoney())
//        //返回已确认的集合
//        bettingListConfirmed.isNotEmpty {
//            val confirmedList = ArrayList<BettingRecordBean>()
//            it.forEach { (_, bettingRecordBean) ->
//                val money = bettingRecordBean.money
//                confirmedList.add(bettingRecordBean)
//                limitMap[bettingRecordBean.bettingArea] = money
//            }
//            block(confirmedList)
//        }.isEmpty {
//            block(null)
//        }
    }

    fun commitBetting(block: (isMoneyEnough: GameAboutModel.BettingState, result: AreaBetConfigBean?) -> Unit) {
        if (!gameAboutModel.isOpen) {
            block(GameAboutModel.BettingState.NO_NETWORK, null)
            return
        }
        if (previousSuccess) {
            val verifyCommitResult = bettingStepList.verifyCommit(currentConfig)
            if (verifyCommitResult != null) {
                block(GameAboutModel.BettingState.OFFSET_MIN, verifyCommitResult)
                return
            }
            //等有返回结果后 再赋值成true
            previousSuccess = false
            val betReq = GameReq.BetReq.newBuilder()
            betReq.setMiniGameId(miniGameId)
            bettingStepList.filter { it.state == BettingStatus.TEMP }
                .map {
                    it.state = BettingStatus.COMMITTING
                    it
                }
                .groupBy(BettingRecordBean::bettingArea)
                .merge()
                .forEach {
                    "注区${it.bettingArea.toastStr},下注金额：${it.money}".loge("GameService-确认下注")
                    val areaBetReq =
                        GameReq.AreaBetReq.newBuilder().setAreaCode(it.bettingArea.number)
                            .setBetScore(it.money).build()
                    betReq.addAreaBet(areaBetReq)

                }
            val build = betReq.build()
            bet(build)
            block(GameAboutModel.BettingState.GO_ON, null)
        }

        /* if (previousSuccess) {
             //只有第一次才判断
             limitMap.forEach {
                 currentConfig?.getBeanById(it.key)?.let { config ->
                     if (it.value < config.minLimit) {
                         block(GameAboutModel.BettingState.OFFSET_MIN, config)
                         return
                     }
                 }
             }
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
             block(GameAboutModel.BettingState.GO_ON, null)
         } */
        else {
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
//        tempMoney = againCountMoney
//        gameAboutModel.setOnceCountMoney(getPanelAllMoney())
//        bettingListTemp copyFrom againBettingMap
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
    fun doubleBetting(block: (isMoneyEnough: GameAboutModel.BettingState, result: MutableMap<Betting, BettingRecordBean?>?, areaLimit: AreaBetConfigBean?) -> Unit) {
       bettingStepList.verifyDouble(currentConfig)?.let {
           if (it.noMoney){
               block(GameAboutModel.BettingState.NO_MONEY, null, null)
           }else{
               block(GameAboutModel.BettingState.OFFSET_MAX, null, it.limitBean)
           }
       }?:run {
           val double = bettingStepList.double()
           bettingStepList.modify()
           block(GameAboutModel.BettingState.GO_ON, double, null)
       }
      /*  if (doubleMoney < balance) {
            val doubleCanOnBean = doubleCanOn()
            doubleCanOnBean?.let {
                block(GameAboutModel.BettingState.OFFSET_MAX, null, it)
            } ?: kotlin.run {
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
                        val uiBean = it.value.copy()
                        uiBean.money = confirmMoney * 2
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
                        val uiBean = it.value.copy()
                        uiBean.money = confirmMoney * 2
                        uiMap[it.key] = uiBean //页面

                        tempCopy[it.key] = it.value.copy()
                    }
                }
                bettingListTemp.clear()
                bettingListTemp copyFrom tempCopy
                tempMoney = confirmTempMoney + confirmMoney + tempMoney * 2
                uiMap.forEach {
                    limitMap[it.key] = it.value.money
                }
                gameAboutModel.setOnceCountMoney(getPanelAllMoney())
                gameAboutModel.changeTempBalance(balance - getPanelAllMoney())
                block(GameAboutModel.BettingState.GO_ON, uiMap, null)
            }
        } else {
            block(GameAboutModel.BettingState.NO_MONEY, null, null)
        }*/
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
//        gameAboutModel.currentStage.observe(owner) {
//            curStage = it
//            checkAgain()
//        }
//        gameAboutModel.onceCountMoney.observe(owner) {
//            onceCountMoney = it
//            checkAgain()
//        }
//        gameAboutModel.isMeetAgain.observe(owner) {
//            isMeetAgain = it
//            checkAgain()
//        }

    }


}