package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.balance
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
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
        if (previousSuccess) {
            currentTempCountMoney += recordBean.money
            //每个注区的总金额 -》currentCountMoney
            //每个注区的临时总金额 -》currentTempCountMoney
            //每个注区的确认总金额 -》currentConfirmCountMoney
            currentCountMoney = currentTempCountMoney + currentConfirmCountMoney

            bettingListTemp.isNotEmpty {
                //已存在同样注区的下注 累计计算已下注金额
                if (it.containsKey(recordBean.bettingArea)) {
                    val existRecord = it[recordBean.bettingArea]
                    val currentMoney = existRecord?.money!! + recordBean.money
                    val countMoney = if (bettingListConfirmed.containsKey(recordBean.bettingArea)) {
                        currentMoney + bettingListConfirmed[recordBean.bettingArea]?.money!!
                    } else {
                        currentMoney
                    }
                    existRecord.money = countMoney
                    "下注194：$existRecord".loge("addBetting")
                    block(balance >= currentCountMoney, existRecord)
                }
                //不存在已下注 注区；直接保存当次下注
                else {
                    bettingListTemp[recordBean.bettingArea] = recordBean
                    "下注200：$recordBean".loge("addBetting")
                    block(balance >= currentCountMoney, recordBean)
                }
            }.isEmpty {
                //新的下注 或者 提交过一次
                bettingListTemp[recordBean.bettingArea] = recordBean
                //判断这次下注是否是已提交过的注区
                if (bettingListConfirmed.containsKey(recordBean.bettingArea)) {
                    //已下注过 存在确认过的金额
                    recordBean.money += bettingListConfirmed[recordBean.bettingArea]?.money!!
                    "下注210：$recordBean".loge("addBetting")
                    block(balance >= currentCountMoney, recordBean)
                } else {
                    "下注213：$recordBean".loge("addBetting")
                    //bettingListTemp 临时下注为空 直接保存当次下注
                    block(balance >= currentCountMoney, recordBean)
                }
            }
//            //根据 续压集合是否为空来判断⬆是不是刚进来
//            againBettingList.isNotEmpty {
//                //当前下注总金额大于余额时 无法续压
//                if (balance >= (currentCountMoney + againCountMoney) * 100) {
//                    gameAboutModel.changeCanAgain(true)
//                } else {
//                    gameAboutModel.changeCanAgain(false)
//                }
//            }
        } else {
            "下注228：上次下注还未返回".loge("addBetting")
            block(false, null)
        }
    }

    /**
     *  取消下注
     *   - 清空临时下注集合
     *   - 返回已确认下注集合
     *  @param block 取消下注后返回已确认的下注
     */
    fun cancelBetting(block: (result: List<BettingRecordBean>?) -> Unit) {
        //重置当前局总下注金额为已提交的金额
        currentCountMoney = currentConfirmCountMoney
        //重置临时总额
        currentTempCountMoney = 0
        //清空临时集合
        bettingListTemp.clear()
        //返回已确认的集合
        bettingListConfirmed.isNotEmpty {
            val confirmedList = ArrayList<BettingRecordBean>()
            it.forEach { (_, bettingRecordBean) ->
                confirmedList.add(bettingRecordBean)
            }
            block(confirmedList)
        }.isEmpty {
            block(null)
        }
    }

    fun commitBetting() {
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
        val build = betReq.build()
        //bet(build)
        //临时本地调试代码
        previousSuccess = true
        bettingListTemp.clear()
    }

    /**
     * 续压
     * 1，上一句的总额就是这一句临时额度 currentTempCountMoney
     * 2，牌面上无下注时才能续压，所以续压的总金额就是当前页面的总金额
     */
    fun againBetting(): Map<Betting, BettingRecordBean>? {
        if (previousSuccess) {
            //1
            currentTempCountMoney = againCountMoney
            //2
            currentCountMoney = againCountMoney
            bettingListTemp.putAll(againBettingList)
            return againBettingList
        } else {
            return null
        }
    }

    /**
     * 加倍
     * 加倍后的总金额算法：@doubleMoney 只是用于传入接口的金额
     *    currentTempCountMoney * 2 + currentConfirmCountMoney
     */
    fun doubleBetting(block: (isMoneyEnough: Boolean, result: Map<Betting, BettingRecordBean>?) -> Unit) {
        if (previousSuccess) {
            //先判断是否足够加倍
            val doubleMoney = currentTempCountMoney * 2 + currentConfirmCountMoney
            if (doubleMoney > balance) {
                bettingListTemp.mapValues {
                    it.value.money *= 2
                    if (bettingListConfirmed.containsKey(it.key)) {
                        it.value.money += 2 * bettingListConfirmed[it.key]?.money!!
                    }
                    it.value
                }
                currentTempCountMoney = doubleMoney
                block(true, bettingListTemp)
            } else {
                block(false, null)
            }

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

}