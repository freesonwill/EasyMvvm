package com.cn.game.sdk2.websocket.imp

import com.cn.game.sdk2.network.code.GameReqCode
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.GameServerMessageConvertFactory
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.balance
import com.cn.game.sdk2.websocket.bean.AreaBetBean
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.cn.game.sdk2.websocket.bean.areaMap
import com.cn.game.sdk2.websocket.calculateArea
import com.cn.game.sdk2.websocket.calculateUserLotteryResult
import com.cn.game.sdk2.websocket.convertBetting
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.interfaces.GameService
import com.cn.game.sdk2.websocket.isBig
import com.cn.game.sdk2.websocket.isCanBetting
import com.cn.game.sdk2.websocket.isDouble
import com.cn.game.sdk2.websocket.isEmpty
import com.cn.game.sdk2.websocket.isNotEmpty
import com.cn.game.sdk2.websocket.miniGameId
import com.cn.game.sdk2.websocket.previousSuccess
import com.cn.game.sdk2.websocket.sum
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.xcjh.base_lib.utils.loge
import game.common.proto.ClientReq
import game.common.proto.ClientRes
import game.mod.proc.yf.proto.req.GameReq
import game.mod.proc.yf.proto.req.GameReq.AreaBetReq
import game.mod.proc.yf.proto.req.GameReq.BetReq
import game.mod.proc.yf.proto.res.GameRes

/**
 * 提供ui层调用的统一对象
 */
class GameServiceImp(private val client: GameSocketClient) : GameService,
    GameServerMessageConvertFactory {

    init {
        GameSocketManager.getInstance()?.setGameServerMessageConvertFactory(this)
    }

    override fun enterInfo() {
        send(
            8,
            GameReqCode.SUB_LOGON_REQ__LOGIN.toShort(),
            ClientReq.PingBackReq.newBuilder().build().toByteArray()
        )
    }

    override fun login(req: ClientReq.LoginReq) {
        val mid: Short = 7
        val sid: Short = 7
        send(mid, sid, req.toByteArray())
    }

    override fun enterGroup(req: GameReq.EnterGroup) {
        send(500, GameReqCode.C2S_ENTER_GROUP.toShort(), req.toByteArray())
    }

    override fun levelGroup() {
        send(500, GameReqCode.C2S_LEAVE_GROUP.toShort(), ByteArray(0))
    }

    override fun enterGame(req: GameReq.EnterMiniGame) {
        send(500, GameReqCode.C2S_ENTER_MINI_GAME.toShort(), req.toByteArray())
    }

    override fun levelGame(req: GameReq.LeaveMiniGamesReq) {
        send(500, GameReqCode.C2S_LEAVE_MINI_GAME.toShort(), req.toByteArray())
    }

    override fun bet(req: GameReq.BetReq) {
        send(500, GameReqCode.C2S_BET.toShort(), req.toByteArray())
    }

    override fun refreshScore() {
        send(500, GameReqCode.C2S_REFRESH_SCORE.toShort(), ByteArray(0))
    }

    override fun ping() {
        send(0, 2, ClientReq.PingBackReq.newBuilder().build().toByteArray())
    }

    private fun send(mid: Short, sid: Short, data: ByteArray) {
        kotlin.runCatching {
            val msg = client.newPack(mid, sid, data, data.size)
            client.send(msg)
        }.recoverCatching {
            it.printStackTrace()
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
     * token失效通知app
     */
    fun pushTokenLoseEffectiveness() {
        appListener?.getTokenLoseEffectiveness()
    }

    /**
     * 当前临时总下注金额
     * - 取消下注 清零
     * - 新的一局开始 清零
     */
    private var currentTempCountMoney = 0

    /**
     * 当前确认总下注金额 已下注部分无法取消
     * - 新的一局开始 清零
     */
    private var currentConfirmCountMoney = 0

    /**
     * 当前总下注金额 界面需要显示
     */
    private var currentCountMoney = 0

    /**
     * 临时下注列表
     */
    private val bettingListTemp: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 已确认下注列表;
     *  - 仅当前局有效，当前局结算后会被清空
     */
    private val bettingListConfirmed: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    /**
     * 续压下注列表;
     *  - 会保存到下一局结算时被下一句数据覆盖
     */
    private var againBettingList: MutableMap<Betting, BettingRecordBean> = mutableMapOf()

    private var againCountMoney = 0

    /**
     * 临时下注
     * 该方法只在服务器状态是游戏中的时候才调用
     * @param recordBean 当前下注对象
     * @param block 下注成功回调
     *          - isMoneyEnough 当次下注余额是否足够
     *          - result 返回当次下注成功的结果，里面有当前的下注的总金额;当此参数为null时，表示上一次下注结果还未返回
     */
    fun addBetting(
        recordBean: BettingRecordBean,
        block: (isMoneyEnough: Boolean, result: BettingRecordBean?) -> Unit
    ) {
        if (previousSuccess) {
            currentTempCountMoney += recordBean.money
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
                    block(balance >= currentCountMoney, existRecord)
                }
                //不存在已下注 注区；直接保存当次下注
                else {
                    bettingListTemp[recordBean.bettingArea] = recordBean
                    block(balance >= currentCountMoney, recordBean)
                }
            }.isEmpty {
                //bettingListTemp 临时下注为空 直接保存当次下注
                bettingListTemp[recordBean.bettingArea] = recordBean
                block(balance >= currentCountMoney, recordBean)
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
            block(false, null)
        }
    }

    /**
     *  取消下注
     *   - 清空临时下注集合
     *   - 返回已确认下注集合
     */
    fun cancelBetting(block: (result: List<BettingRecordBean>?) -> Unit) {
        currentTempCountMoney = 0
        bettingListTemp.clear()
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
        var index = 0
        val betReq = BetReq.newBuilder()
        betReq.setMiniGameId(miniGameId)
        bettingListTemp.forEach { (betting, bettingRecordBean) ->
            "注区${betting.number},下注金额：${bettingRecordBean.money}".loge("GameService-确认下注")
            val areaBetReq = AreaBetReq.newBuilder().setAreaCode(betting.number)
                .setBetScore(bettingRecordBean.money).build()
            betReq.setAreaBet(index, areaBetReq)
            index++
        }
        val build = betReq.build()
        bet(build)
    }

    /**
     * 续压
     *
     */
    fun againBetting(): Map<Betting, BettingRecordBean>? {
        if (previousSuccess) {
            bettingListTemp.putAll(againBettingList)
            return againBettingList
        } else {
            return null
        }
    }

    fun doubleBetting() {
        if (previousSuccess) {

        }
    }

    override fun loginSuccess(afterLoginSuccess: ClientRes.InfoAfterLoginSuccess) {

    }

    override fun loginError(errorMessage: ClientRes.ErrorMessage) {

    }

    override fun enterInfo(enterInfo: GameRes.EnterInfo) {
        balance = enterInfo.self.score.toInt()
        gameAboutModel.changeBalance(enterInfo.self.score.toInt())

    }

    override fun groupInfo(groupInfo: GameRes.GroupInfo) {
        val miniGameBasicInfo = groupInfo.miniGameBasicInfoListList[0]
        miniGameId = miniGameBasicInfo.miniGameId
        gameAboutModel.miniGameId = miniGameId
        gameAboutModel.countDown = miniGameBasicInfo.countDown
        val roundInfoListList = miniGameBasicInfo.trend.roundInfoListList
        val roundHistoryList = ArrayList<RoundInfoBean>()
        roundInfoListList.forEach {
            val elements = it.performsList[0].performResultList
            roundHistoryList.add(
                RoundInfoBean(
                    it.roundId, elements, elements.sum(), elements.isBig(), elements.isDouble()
                )
            )
        }
        gameAboutModel.addHistoryRounds(roundHistoryList)
        when (miniGameBasicInfo.stage) {
            1 -> gameAboutModel.changeStage(GameAboutModel.Stage.NEW)
            2 -> gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
            3 -> gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        }
    }

    override fun leaveGroup(leave: GameRes.LeaveGroup) {

    }

    override fun leaveMiniGameInfo(miniGame: GameRes.LeaveMiniGames) {

    }

    override fun enterMiniGameInfo(miniGame: GameRes.EnterMiniGameInfo) {
        miniGameId = miniGame.miniGameId
    }

    override fun miniGameBetResult(result: GameRes.MyMiniGameBetResult) {
        previousSuccess = true
        //result = 0 成功 1 余额不住 3超时
        when (result.betResultInfoListList[0].result) {
            0 -> {
                //下注成功后 保存当前下注总额为已确认下注金额；并将当前下注总额清空
                currentConfirmCountMoney = currentCountMoney
                currentCountMoney = 0
                previousSuccess = true
                bettingListTemp.forEach { (betting, temBean) ->
                    if (bettingListConfirmed.containsKey(betting)) {
                        temBean.money += bettingListConfirmed[betting]?.money!!
                    }
                    bettingListConfirmed[betting] = temBean
                }
                bettingListTemp.clear()
            }

            1 -> {

            }

            2 -> {}
            else -> {}
        }

    }

    override fun refreshUserProperties(userScore: GameRes.RefreshUserScore) {
        balance = userScore.score.toInt()
        gameAboutModel.changeBalance(balance)
    }

    override fun beginRound(round: GameRes.BeginNewRound) {
        isCanBetting = true
        gameAboutModel.miniGameId = round.miniGameId
        gameAboutModel.roundId = round.roundId //期号
        gameAboutModel.countDown = round.countDown //当前阶段剩余时间倒计时
        gameAboutModel.changeStage(GameAboutModel.Stage.NEW)

        againBettingList.isNotEmpty {
            if (againCountMoney <= balance) gameAboutModel.changeCanAgain(true)
            else gameAboutModel.changeCanAgain(false)
        }.isEmpty {
            gameAboutModel.changeCanAgain(false)
        }
        //重置上一局的所有钱
        currentCountMoney = 0
        currentConfirmCountMoney = 0
        currentTempCountMoney = 0

    }

    override fun beginDeal(round: GameRes.BeginDeal) {
        isCanBetting = false
        gameAboutModel.miniGameId = round.miniGameId
        gameAboutModel.roundId = round.roundId //期号
        gameAboutModel.countDown = round.countDown //当前阶段剩余时间倒计时
        gameAboutModel.changeStage(GameAboutModel.Stage.DEAL)
    }

    override fun beginSettle(settle: GameRes.BeginSettle) {
        isCanBetting = false
        gameAboutModel.miniGameId = settle.miniGameId
        gameAboutModel.roundId = settle.roundInfo.roundId //期号
        gameAboutModel.countDown = settle.countDown //当前阶段剩余时间倒计时
        if (settle.winScore > 0) {
            //如果中奖 就计算净收入
            gameAboutModel.netIncome = settle.winScore - (currentConfirmCountMoney * 100)
        }
        //结束时更新余额
        balance = balance + settle.winScore - (currentConfirmCountMoney * 100)
        //开奖号码
        val lotteryNumbers = settle.roundInfo.performsList[0].performResultList
        //中奖注区
        val lotteryResultList = lotteryNumbers.calculateArea()
        //添加历史记录
        gameAboutModel.addHistoryRound(
            RoundInfoBean(
                settle.roundInfo.roundId,
                lotteryNumbers,
                lotteryNumbers.sum(),
                lotteryNumbers.isBig(),
                lotteryNumbers.isDouble()
            )
        )

        gameAboutModel.lotteryResultList = lotteryResultList
        //计算用户中奖注区及金额
        gameAboutModel.userLotteryResult =
            lotteryResultList.calculateUserLotteryResult(bettingListConfirmed)
        //跟新阶段
        gameAboutModel.changeStage(GameAboutModel.Stage.SETTLE)
        //清空本局已下注数据，并复制到续压集合里
        againBettingList = bettingListConfirmed
        againCountMoney = currentConfirmCountMoney
        bettingListConfirmed.clear()
    }

    override fun syncAreaBetInfoBack(syncAreaBetInfo: GameRes.SyncAreaBetInfo) {
        val syncAreaList = ArrayList<AreaBetBean>()
        syncAreaBetInfo.areaBetsList.forEach {
            syncAreaList.add(AreaBetBean(it.areaCode.convertBetting()!!, it.betScore, it.userCount))
        }
        gameAboutModel.changeAreaBetInfo(syncAreaList)
    }

    override fun clearTrendsBackBlock(clearTrends: GameRes.ClearTrends) {
        gameAboutModel.clearTrends(clearTrends.miniGameIdsList)
        gameAboutModel.addHistoryRounds(listOfNotNull())
    }

    override fun errorMessage(errorMessage: ClientRes.ErrorMessage) {
        val code = errorMessage.code
        val desc = errorMessage.desc

    }

    override fun tokenLoseEffectiveness() {
        appListener?.getTokenLoseEffectiveness()
    }

}